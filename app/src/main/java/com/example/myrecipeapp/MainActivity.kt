package com.example.myrecipeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myrecipeapp.ui.screen.RecipeDetailScreen
import com.example.myrecipeapp.ui.screen.RecipeListScreen
import com.example.myrecipeapp.ui.screen.SplashScreen
import com.example.myrecipeapp.ui.theme.MyRecipeAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyRecipeAppTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen(onSplashFinished = { showSplash = false })
                } else {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "list") {
                        composable("list") {
                            RecipeListScreen(
                                onRecipeClick = { recipeId ->
                                    navController.navigate("detail/$recipeId")
                                }
                            )
                        }
                        composable("detail/{recipeId}") { backStackEntry ->
                            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: return@composable
                            RecipeDetailScreen(
                                recipeId = recipeId,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
