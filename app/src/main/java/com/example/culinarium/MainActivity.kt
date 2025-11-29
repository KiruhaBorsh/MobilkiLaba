package com.example.culinarium

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import com.example.culinarium.data.Recipe
import com.example.culinarium.data.RecipeRepository
import com.example.culinarium.ui.screens.*
import com.example.culinarium.ui.theme.CulinariumTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val recipeRepository = RecipeRepository()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CulinariumTheme {
                RecipeApp(
                    recipeRepository = recipeRepository,
                    onLanguageChanged = {
                        // Перезапуск активности для применения изменений локали
                        recreate()
                    }
                )
            }
        }
    }
}

@Composable
fun RecipeApp(
    recipeRepository: RecipeRepository,
    onLanguageChanged: () -> Unit = {}
) {
    //Управление состоянием навигации между экранами
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Recipes) }
    var selectedRecipeId by remember { mutableStateOf<String?>(null) }
    var editingRecipe by remember { mutableStateOf<Recipe?>(null) }
    
    fun toggleLanguage() {
        val current = AppCompatDelegate.getApplicationLocales()
        val isRu = current.isEmpty || current[0]?.language == "ru"
        val newLocales = if (isRu) {
            LocaleListCompat.create(Locale("en", "US"))
        } else {
            LocaleListCompat.create(Locale("ru", "RU"))
        }
        AppCompatDelegate.setApplicationLocales(newLocales)
        onLanguageChanged()
    }

    when (currentScreen) {
        Screen.Recipes -> {
            RecipesScreen(
                onRecipeClick = { recipeId ->
                    selectedRecipeId = recipeId
                    currentScreen = Screen.RecipeDetail
                },
                onAddRecipeClick = {
                    editingRecipe = null
                    currentScreen = Screen.AddEditRecipe
                },
                recipeRepository = recipeRepository,
                onToggleLanguage = { toggleLanguage() }
            )
        }
        Screen.RecipeDetail -> {
            selectedRecipeId?.let { recipeId ->
                RecipeDetailScreen(
                    recipeId = recipeId,
                    onEditClick = { id ->
                        editingRecipe = recipeRepository.getRecipeById(id)
                        currentScreen = Screen.AddEditRecipe
                    },
                    onBackClick = {
                        currentScreen = Screen.Recipes
                    },
                    onDeleteClick = { id ->
                        recipeRepository.deleteRecipe(id)
                        currentScreen = Screen.Recipes
                    },
                    recipeRepository = recipeRepository,
                    onToggleLanguage = { toggleLanguage() }
                )
            }
        }
        Screen.AddEditRecipe -> {
            AddEditRecipeScreen(
                recipeId = editingRecipe?.id,
                onBackClick = {
                    currentScreen = Screen.Recipes
                },
                onSaveClick = { recipe ->
                    if (editingRecipe != null) {
                        recipeRepository.updateRecipe(recipe)
                    } else {
                        recipeRepository.addRecipe(recipe)
                    }
                    currentScreen = Screen.Recipes
                },
                recipe = editingRecipe,
                onToggleLanguage = { toggleLanguage() }
            )
        }
    }
}

sealed class Screen {
    object Recipes : Screen()
    object RecipeDetail : Screen()
    object AddEditRecipe : Screen()
}