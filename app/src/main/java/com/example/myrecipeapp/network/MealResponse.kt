package com.example.myrecipeapp.network

import com.google.gson.annotations.SerializedName

// API에서 오는 레시피 한 개 (JSON 필드명 → Kotlin 변수명)
data class MealDto(
    @SerializedName("idMeal") val id: String,
    @SerializedName("strMeal") val title: String,
    @SerializedName("strCategory") val category: String? = null,
    @SerializedName("strArea") val area: String? = null,
    @SerializedName("strMealThumb") val thumbnail: String? = null,
    @SerializedName("strInstructions") val instructions: String? = null,

    // 재료 (API는 strIngredient1 ~ strIngredient20 로 따로 보냄)
    @SerializedName("strIngredient1") val ingredient1: String? = null,
    @SerializedName("strIngredient2") val ingredient2: String? = null,
    @SerializedName("strIngredient3") val ingredient3: String? = null,
    @SerializedName("strIngredient4") val ingredient4: String? = null,
    @SerializedName("strIngredient5") val ingredient5: String? = null,
    @SerializedName("strIngredient6") val ingredient6: String? = null,
    @SerializedName("strIngredient7") val ingredient7: String? = null,
    @SerializedName("strIngredient8") val ingredient8: String? = null,
    @SerializedName("strIngredient9") val ingredient9: String? = null,
    @SerializedName("strIngredient10") val ingredient10: String? = null,
    @SerializedName("strIngredient11") val ingredient11: String? = null,
    @SerializedName("strIngredient12") val ingredient12: String? = null,
    @SerializedName("strIngredient13") val ingredient13: String? = null,
    @SerializedName("strIngredient14") val ingredient14: String? = null,
    @SerializedName("strIngredient15") val ingredient15: String? = null,
    @SerializedName("strIngredient16") val ingredient16: String? = null,
    @SerializedName("strIngredient17") val ingredient17: String? = null,
    @SerializedName("strIngredient18") val ingredient18: String? = null,
    @SerializedName("strIngredient19") val ingredient19: String? = null,
    @SerializedName("strIngredient20") val ingredient20: String? = null,

    // 재료 양 (strMeasure1 ~ strMeasure20)
    @SerializedName("strMeasure1") val measure1: String? = null,
    @SerializedName("strMeasure2") val measure2: String? = null,
    @SerializedName("strMeasure3") val measure3: String? = null,
    @SerializedName("strMeasure4") val measure4: String? = null,
    @SerializedName("strMeasure5") val measure5: String? = null,
    @SerializedName("strMeasure6") val measure6: String? = null,
    @SerializedName("strMeasure7") val measure7: String? = null,
    @SerializedName("strMeasure8") val measure8: String? = null,
    @SerializedName("strMeasure9") val measure9: String? = null,
    @SerializedName("strMeasure10") val measure10: String? = null,
    @SerializedName("strMeasure11") val measure11: String? = null,
    @SerializedName("strMeasure12") val measure12: String? = null,
    @SerializedName("strMeasure13") val measure13: String? = null,
    @SerializedName("strMeasure14") val measure14: String? = null,
    @SerializedName("strMeasure15") val measure15: String? = null,
    @SerializedName("strMeasure16") val measure16: String? = null,
    @SerializedName("strMeasure17") val measure17: String? = null,
    @SerializedName("strMeasure18") val measure18: String? = null,
    @SerializedName("strMeasure19") val measure19: String? = null,
    @SerializedName("strMeasure20") val measure20: String? = null,
) {
    // 재료 목록을 깔끔하게 리스트로 변환
    fun getIngredients(): List<Pair<String, String>> {
        val names = listOf(
            ingredient1, ingredient2, ingredient3, ingredient4, ingredient5,
            ingredient6, ingredient7, ingredient8, ingredient9, ingredient10,
            ingredient11, ingredient12, ingredient13, ingredient14, ingredient15,
            ingredient16, ingredient17, ingredient18, ingredient19, ingredient20
        )
        val measures = listOf(
            measure1, measure2, measure3, measure4, measure5,
            measure6, measure7, measure8, measure9, measure10,
            measure11, measure12, measure13, measure14, measure15,
            measure16, measure17, measure18, measure19, measure20
        )
        return names.zip(measures)
            .filter { (name, _) -> !name.isNullOrBlank() }
            .map { (name, measure) -> Pair(name!!, measure.orEmpty().trim()) }
    }
}

// /search.php 와 /filter.php 응답 wrapper
data class MealsResponse(
    @SerializedName("meals") val meals: List<MealDto>?
)

// /categories.php 응답용 카테고리 DTO
data class CategoryDto(
    @SerializedName("idCategory") val id: String,
    @SerializedName("strCategory") val name: String,
    @SerializedName("strCategoryThumb") val thumbnail: String
)

// /categories.php 응답 wrapper
data class CategoriesResponse(
    @SerializedName("categories") val categories: List<CategoryDto>?
)