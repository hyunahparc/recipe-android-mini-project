package com.example.myrecipeapp.repository

import com.example.myrecipeapp.model.Category
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

    suspend fun getCategories(): Result<List<Category>> {
        return try {
            val response = RetrofitInstance.api.getCategories()
            val categories = response.categories?.map { dto ->
                Category(id = dto.id, name = dto.name, thumbnail = dto.thumbnail)
            } ?: emptyList()
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun filterByCategory(category: String): Result<List<Recipe>> {
        return try {
            val response = RetrofitInstance.api.filterByCategory(category)
            val recipes = response.meals?.map { dto ->
                Recipe(
                    id = dto.id,
                    title = dto.title,
                    category = category,
                    area = dto.area.orEmpty(),
                    thumbnail = dto.thumbnail.orEmpty()
                )
            } ?: emptyList()
            Result.success(recipes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
