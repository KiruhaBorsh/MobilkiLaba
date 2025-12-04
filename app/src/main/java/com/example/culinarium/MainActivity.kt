package com.example.culinarium

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.example.culinarium.data.Recipe
import com.example.culinarium.data.RecipeRepository
import com.example.culinarium.ui.screens.*
import com.example.culinarium.ui.theme.CulinariumTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val recipeRepository = RecipeRepository()
    
    override fun attachBaseContext(newBase: Context) {
        val locale = getSavedLocale(newBase)
        val contextWithLocale = updateLocale(newBase, locale)
        super.attachBaseContext(contextWithLocale)
    }
    
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
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val languageToggleLabel = remember(configuration) {
        getLanguageToggleLabel(configuration)
    }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Recipes) }
    var selectedRecipeId by remember { mutableStateOf<String?>(null) }
    var editingRecipe by remember { mutableStateOf<Recipe?>(null) }
    
    fun toggleLanguage() {
        val currentLocale = getSavedLocale(context)
        val newLocale = if (currentLocale.language == "ru") {
            Locale("en")
        } else {
            Locale("ru")
        }
        saveLocale(context, newLocale)
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
                onToggleLanguage = { toggleLanguage() },
                languageToggleLabel = languageToggleLabel
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
                    onToggleLanguage = { toggleLanguage() },
                    languageToggleLabel = languageToggleLabel
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
                onToggleLanguage = { toggleLanguage() },
                languageToggleLabel = languageToggleLabel
            )
        }
    }
}

private const val PREFS_NAME = "settings"
private const val KEY_LANGUAGE = "language"

private fun getSavedLocale(context: Context): Locale {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val lang = prefs.getString(KEY_LANGUAGE, null)
    return if (lang.isNullOrBlank()) {
        Locale.getDefault()
    } else {
        Locale(lang)
    }
}

private fun saveLocale(context: Context, locale: Locale) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putString(KEY_LANGUAGE, locale.language).apply()
}

private fun updateLocale(context: Context, locale: Locale): Context {
    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    return context.createConfigurationContext(config)
}

private fun getLanguageToggleLabel(configuration: Configuration): String {
    val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        configuration.locales[0]
    } else {
        @Suppress("DEPRECATION")
        configuration.locale
    }
    return if (locale?.language == "ru") "EN" else "RU"
}

sealed class Screen {
    object Recipes : Screen()
    object RecipeDetail : Screen()
    object AddEditRecipe : Screen()
}