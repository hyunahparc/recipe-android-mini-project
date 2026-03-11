package com.example.myrecipeapp.network

import retrofit2.http.GET
import retrofit2.http.Query

interface MealApiService {

    // 레시피 검색 (제목으로)
    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String): MealsResponse

    // 레시피 상세 조회 (id로)
    @GET("lookup.php")
    suspend fun getMealById(@Query("i") id: String): MealsResponse

    // 카테고리 목록
    @GET("categories.php")
    suspend fun getCategories(): CategoriesResponse

    // 카테고리로 필터
    @GET("filter.php")
    suspend fun filterByCategory(@Query("c") category: String): MealsResponse
}