package com.example.myrecipeapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecipeDao {

    // 레시피 저장 (이미 있으면 덮어쓰기)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)

    // 레시피 한 개 저장
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' LIMIT :limit OFFSET :offset")
    suspend fun searchRecipesPaged(query: String, limit: Int, offset: Int): List<RecipeEntity>

    @Query("SELECT * FROM recipes WHERE category = :category LIMIT :limit OFFSET :offset")
    suspend fun getRecipesByCategoryPaged(category: String, limit: Int, offset: Int): List<RecipeEntity>

    // id로 레시피 조회
    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: String): RecipeEntity?

    // 오래된 캐시 삭제 (24시간 이상)
    @Query("DELETE FROM recipes WHERE cachedAt < :threshold")
    suspend fun deleteOldRecipes(threshold: Long)

    @Query("SELECT MIN(cachedAt) FROM recipes WHERE title LIKE '%' || :query || '%'")
    suspend fun getOldestCacheTime(query: String): Long?
}

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<CategoryEntity>
}