package com.example.myrecipeapp.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.myrecipeapp.model.Recipe
import com.example.myrecipeapp.viewmodel.RecipeListViewModel

@Composable
fun RecipeListScreen(
    onRecipeClick: (String) -> Unit = {},
    viewModel: RecipeListViewModel = viewModel()
) {
    val recipes by viewModel.recipes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()
    val error by viewModel.error.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= listState.layoutInfo.totalItemsCount - 5
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && hasMore) viewModel.loadNextPage()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text(
            text = "Recipes",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            placeholder = { Text("Search recipes...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(12.dp)
        )

        // Category filter chips
        if (categories.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category.name,
                        onClick = { viewModel.onCategorySelected(category.name) },
                        label = { Text(category.name) }
                    )
                }
            }
        }

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = error!!, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadRecipes() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            recipes.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No recipes found.")
                }
            }
            else -> {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recipes) { recipe ->
                        RecipeCard(recipe = recipe, onClick = { onRecipeClick(recipe.id) })
                    }
                    if (isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = recipe.thumbnail,
                contentDescription = recipe.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = recipe.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}