package com.example.myrecipeapp.repository

import com.example.myrecipeapp.model.Ingredient
import com.example.myrecipeapp.model.Recipe
import com.example.myrecipeapp.network.RetrofitInstance

class RecipeRepository {

    suspend fun searchRecipes(query: String): Result<List<Recipe>> {
        return try {
            val response = RetrofitInstance.api.searchMeals(query)
            val recipes = response.meals?.map { dto ->
                Recipe(
                    id = dto.id,
                    title = dto.title,
                    category = dto.category.orEmpty(),
                    area = dto.area.orEmpty(),
                    thumbnail = dto.thumbnail.orEmpty(),
                    instructions = dto.instructions.orEmpty(),
                    ingredients = dto.getIngredients().map { (name, measure) ->
                        Ingredient(name, measure)
                    }
                )
            } ?: emptyList()
            Result.success(recipes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
