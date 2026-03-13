package com.example.myrecipeapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val area: String,
    val thumbnail: String,
    val instructions: String,
    val ingredientsJson: String, // Ingredient 리스트를 JSON 문자열로 저장
    val cachedAt: Long = System.currentTimeMillis() // 캐시된 시간
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val thumbnail: String
)