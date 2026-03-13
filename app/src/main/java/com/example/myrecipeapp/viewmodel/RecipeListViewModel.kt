package com.example.myrecipeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myrecipeapp.database.AppDatabase
import com.example.myrecipeapp.model.Category
import com.example.myrecipeapp.model.Recipe
import com.example.myrecipeapp.repository.RecipeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecipeListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RecipeRepository(AppDatabase.getInstance(application))

    companion object {
        private const val PAGE_SIZE = 30
    }

    private var currentPage = 0
    private var currentQuery = "chicken"
    private var currentCategory: String? = null

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore

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
        _selectedCategory.value = null
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(800)
            loadRecipes(query.ifBlank { "chicken" })
        }
    }

    fun onCategorySelected(category: String) {
        if (_selectedCategory.value == category) {
            _selectedCategory.value = null
            loadRecipes(_searchQuery.value.ifBlank { "chicken" })
        } else {
            _selectedCategory.value = category
            currentCategory = category
            currentPage = 0
            _searchQuery.value = ""
            viewModelScope.launch {
                _isLoading.value = true
                _error.value = null
                val result = repository.filterByCategoryPaged(category, page = 0)
                result.onSuccess {
                    _recipes.value = it
                    _hasMore.value = it.size >= PAGE_SIZE
                }
                result.onFailure { _error.value = "Could not load recipes. Please check your connection." }
                _isLoading.value = false
            }
        }
    }

    fun loadRecipes(query: String = "chicken") {
        currentQuery = query
        currentPage = 0
        currentCategory = null
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.searchRecipesPaged(query, page = 0)
            result.onSuccess {
                _recipes.value = it
                _hasMore.value = it.size >= PAGE_SIZE
            }
            result.onFailure { _error.value = "Could not load recipes. Please check your connection." }
            _isLoading.value = false
        }
    }

    fun loadNextPage() {
        if (_isLoadingMore.value || !_hasMore.value) return
        viewModelScope.launch {
            _isLoadingMore.value = true
            currentPage++
            val result = if (currentCategory != null) {
                repository.filterByCategoryPaged(currentCategory!!, page = currentPage)
            } else {
                repository.searchRecipesPaged(currentQuery, page = currentPage)
            }
            result.onSuccess { newItems ->
                if (newItems.isEmpty()) {
                    _hasMore.value = false
                } else {
                    _recipes.value = _recipes.value + newItems
                    _hasMore.value = newItems.size >= PAGE_SIZE
                }
            }
            result.onFailure { currentPage-- }
            _isLoadingMore.value = false
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val result = repository.getCategories()
            result.onSuccess { _categories.value = it }
        }
    }
}