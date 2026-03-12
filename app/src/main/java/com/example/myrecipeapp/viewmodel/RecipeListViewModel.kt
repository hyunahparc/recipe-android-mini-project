package com.example.myrecipeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myrecipeapp.model.Recipe
import com.example.myrecipeapp.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecipeListViewModel : ViewModel() {

    private val repository = RecipeRepository()

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadRecipes("chicken") // 앱 시작시 기본 검색
    }

    fun loadRecipes(query: String = "chicken") {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.searchRecipes(query)
            result.onSuccess { _recipes.value = it }
            result.onFailure { _error.value = "레시피를 불러오지 못했어요. 인터넷 연결을 확인해주세요." }
            _isLoading.value = false
        }
    }
}