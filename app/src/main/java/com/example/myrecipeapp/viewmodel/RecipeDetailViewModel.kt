package com.example.myrecipeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myrecipeapp.database.AppDatabase
import com.example.myrecipeapp.model.Recipe
import com.example.myrecipeapp.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecipeDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RecipeRepository(AppDatabase.getInstance(application))

    private val _recipe = MutableStateFlow<Recipe?>(null)
    val recipe: StateFlow<Recipe?> = _recipe

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadRecipe(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.getRecipeById(id)
            result.onSuccess { _recipe.value = it }
            result.onFailure { _error.value = "Could not load recipe details." }
            _isLoading.value = false
        }
    }
}
