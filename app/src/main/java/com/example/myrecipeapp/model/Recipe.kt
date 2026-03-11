package com.example.myrecipeapp.model

data class Recipe(
    val id: String,
    val title: String,
    val category: String,
    val area: String,
    val thumbnail: String,
    val instructions: String = "",
    val ingredients: List<Ingredient> = emptyList()
)