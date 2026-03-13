package com.example.myrecipeapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.myrecipeapp.viewmodel.RecipeDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: String,
    onBack: () -> Unit,
    viewModel: RecipeDetailViewModel = viewModel()
) {
    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    val recipe by viewModel.recipe.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recipe?.title ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = error!!, color = MaterialTheme.colorScheme.error)
                }
            }
            recipe != null -> {
                val r = recipe!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    AsyncImage(
                        model = r.thumbnail,
                        contentDescription = r.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = r.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (r.category.isNotBlank()) {
                                AssistChip(
                                    onClick = {},
                                    label = { Text(r.category) }
                                )
                            }
                            if (r.area.isNotBlank()) {
                                AssistChip(
                                    onClick = {},
                                    label = { Text(r.area) }
                                )
                            }
                        }

                        if (r.ingredients.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Ingredients",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            r.ingredients.forEach { ingredient ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = ingredient.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = ingredient.measure,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                HorizontalDivider()
                            }
                        }

                        if (r.instructions.isNotBlank()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Instructions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = r.instructions,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}
