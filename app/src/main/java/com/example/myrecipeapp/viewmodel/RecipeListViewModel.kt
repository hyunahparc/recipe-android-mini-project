package com.example.myrecipeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myrecipeapp.model.Category
import com.example.myrecipeapp.model.Recipe
import com.example.myrecipeapp.repository.RecipeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecipeListViewModel : ViewModel() {

    private val repository = RecipeRepository()

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private var searchJob: Job? = null

    init {
        loadRecipes("chicken")
        loadCategories()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _selectedCategory.value = null // reset category when searching
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(800)
            loadRecipes(query.ifBlank { "chicken" })
        }
    }

    fun onCategorySelected(category: String) {
        if (_selectedCategory.value == category) {
            // deselect if already selected
            _selectedCategory.value = null
            loadRecipes(_searchQuery.value.ifBlank { "chicken" })
        } else {
            _selectedCategory.value = category
            _searchQuery.value = ""
            viewModelScope.launch {
                _isLoading.value = true
                _error.value = null
                val result = repository.filterByCategory(category)
                result.onSuccess { _recipes.value = it }
                result.onFailure { _error.value = "Could not load recipes. Please check your connection." }
                _isLoading.value = false
            }
        }
    }

    fun loadRecipes(query: String = "chicken") {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.searchRecipes(query)
            result.onSuccess { _recipes.value = it }
            result.onFailure { _error.value = "Could not load recipes. Please check your connection." }
            _isLoading.value = false
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val result = repository.getCategories()
            result.onSuccess { _categories.value = it }
        }
    }
}