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

    // 제목으로 검색
    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%'")
    suspend fun searchRecipes(query: String): List<RecipeEntity>

    // 카테고리로 필터
    @Query("SELECT * FROM recipes WHERE category = :category")
    suspend fun getRecipesByCategory(category: String): List<RecipeEntity>

    // id로 레시피 조회
    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: String): RecipeEntity?

    // 모든 레시피 조회
    @Query("SELECT * FROM recipes")
    suspend fun getAllRecipes(): List<RecipeEntity>

    // 오래된 캐시 삭제 (24시간 이상)
    @Query("DELETE FROM recipes WHERE cachedAt < :threshold")
    suspend fun deleteOldRecipes(threshold: Long)
}

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<CategoryEntity>
}