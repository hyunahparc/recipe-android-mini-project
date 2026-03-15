package com.example.myrecipeapp.repository

import com.example.myrecipeapp.database.AppDatabase
import com.example.myrecipeapp.database.CategoryEntity
import com.example.myrecipeapp.database.RecipeEntity
import com.example.myrecipeapp.model.Category
import com.example.myrecipeapp.model.Ingredient
import com.example.myrecipeapp.model.Recipe
import com.example.myrecipeapp.network.RetrofitInstance
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RecipeRepository(private val db: AppDatabase) {

    private val gson = Gson()
    private val staleThreshold = 60 * 60 * 1000L // 1시간

    private suspend fun isCacheStale(query: String): Boolean {
        val oldest = db.recipeDao().getOldestCacheTime(query) ?: return true
        return System.currentTimeMillis() - oldest > staleThreshold
    }

    suspend fun getCategories(): Result<List<Category>> {
        return try {
            // Fetch from API
            val response = RetrofitInstance.api.getCategories()
            val categories = response.categories?.map { dto ->
                Category(id = dto.id, name = dto.name, thumbnail = dto.thumbnail)
            } ?: emptyList()

            // Save to DB
            db.categoryDao().insertCategories(categories.map { it.toEntity() })

            Result.success(categories)
        } catch (e: Exception) {
            // Fall back to DB on API failure
            val cached = db.categoryDao().getAllCategories()
            if (cached.isNotEmpty()) {
                Result.success(cached.map { it.toCategory() })
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun getRecipeById(id: String): Result<Recipe> {
        // Try DB first
        val cached = db.recipeDao().getRecipeById(id)
        if (cached != null && cached.instructions.isNotBlank()) {
            return Result.success(cached.toRecipe())
        }
        // Fetch full details from API
        return try {
            val response = RetrofitInstance.api.getMealById(id)
            val dto = response.meals?.firstOrNull()
                ?: return Result.failure(Exception("Recipe not found"))
            val recipe = Recipe(
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
            db.recipeDao().insertRecipe(recipe.toEntity())
            Result.success(recipe)
        } catch (e: Exception) {
            if (cached != null) Result.success(cached.toRecipe())
            else Result.failure(e)
        }
    }

    suspend fun searchRecipesPaged(query: String, page: Int, pageSize: Int = 30): Result<List<Recipe>> {
        if (page == 0 && isCacheStale(query)) {
            try {
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
                db.recipeDao().insertRecipes(recipes.map { it.toEntity() })
                val threshold = System.currentTimeMillis() - 24 * 60 * 60 * 1000L
                db.recipeDao().deleteOldRecipes(threshold)
            } catch (_: Exception) { /* use cache */ }
        }
        val cached = db.recipeDao().searchRecipesPaged(query, pageSize, page * pageSize)
        return if (cached.isNotEmpty()) Result.success(cached.map { it.toRecipe() })
        else if (page > 0) Result.success(emptyList())
        else Result.failure(Exception("No results found"))
    }

    suspend fun filterByCategoryPaged(category: String, page: Int, pageSize: Int = 30): Result<List<Recipe>> {
        if (page == 0) {
            try {
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
                db.recipeDao().insertRecipes(recipes.map { it.toEntity() })
            } catch (_: Exception) { /* use cache */ }
        }
        val cached = db.recipeDao().getRecipesByCategoryPaged(category, pageSize, page * pageSize)
        return if (cached.isNotEmpty()) Result.success(cached.map { it.toRecipe() })
        else if (page > 0) Result.success(emptyList())
        else Result.failure(Exception("No results found"))
    }

    // Recipe → RecipeEntity (convert to DB entity)
    private fun Recipe.toEntity() = RecipeEntity(
        id = id,
        title = title,
        category = category,
        area = area,
        thumbnail = thumbnail,
        instructions = instructions,
        ingredientsJson = gson.toJson(ingredients)
    )

    // RecipeEntity → Recipe (convert back from DB entity)
    private fun RecipeEntity.toRecipe(): Recipe {
        val type = object : TypeToken<List<Ingredient>>() {}.type
        val ingredients: List<Ingredient> = gson.fromJson(ingredientsJson, type) ?: emptyList()
        return Recipe(
            id = id,
            title = title,
            category = category,
            area = area,
            thumbnail = thumbnail,
            instructions = instructions,
            ingredients = ingredients
        )
    }

    // Category → CategoryEntity
    private fun Category.toEntity() = CategoryEntity(id = id, name = name, thumbnail = thumbnail)

    // CategoryEntity → Category
    private fun CategoryEntity.toCategory() = Category(id = id, name = name, thumbnail = thumbnail)
}