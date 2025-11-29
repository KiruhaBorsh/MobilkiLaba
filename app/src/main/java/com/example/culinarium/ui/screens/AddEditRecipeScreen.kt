package com.example.culinarium.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.culinarium.R
import com.example.culinarium.data.Recipe
import com.example.culinarium.data.Difficulty
import com.example.culinarium.data.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecipeScreen(
    recipeId: String?,
    onBackClick: () -> Unit,
    onSaveClick: (Recipe) -> Unit,
    recipe: Recipe?,
    onToggleLanguage: () -> Unit
) {
    var title by remember { mutableStateOf(recipe?.title ?: "") }
    var description by remember { mutableStateOf(recipe?.description ?: "") }
    var ingredients by remember { mutableStateOf(recipe?.ingredients?.toMutableList() ?: mutableListOf("")) }
    var instructions by remember { mutableStateOf(recipe?.instructions?.toMutableList() ?: mutableListOf("")) }
    var cookingTime by remember { mutableStateOf(recipe?.cookingTime?.toString() ?: "") }
    var servings by remember { mutableStateOf(recipe?.servings?.toString() ?: "") }
    var difficulty by remember { mutableStateOf(recipe?.difficulty ?: Difficulty.EASY) }
    var category by remember { mutableStateOf(recipe?.category ?: Category.BREAKFAST) }

    val isEditMode = recipeId != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (isEditMode) stringResource(R.string.edit_recipe_title) 
                        else stringResource(R.string.new_recipe)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    TextButton(onClick = onToggleLanguage) { Text("EN") }
                    TextButton(
                        onClick = {
                            if (isValidRecipe(title, description, ingredients, instructions, cookingTime, servings)) {
                                val newRecipe = Recipe(
                                    id = recipeId ?: "",
                                    title = title,
                                    description = description,
                                    ingredients = ingredients.filter { it.isNotBlank() },
                                    instructions = instructions.filter { it.isNotBlank() },
                                    cookingTime = cookingTime.toIntOrNull() ?: 0,
                                    servings = servings.toIntOrNull() ?: 1,
                                    difficulty = difficulty,
                                    category = category,
                                    isFavorite = recipe?.isFavorite ?: false,
                                    createdAt = recipe?.createdAt ?: System.currentTimeMillis()
                                )
                                onSaveClick(newRecipe)
                            }
                        }
                    ) {
                        Text(stringResource(R.string.save_recipe))
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.recipe_title)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    )
                )
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.recipe_description)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Default
                    )
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = cookingTime,
                        onValueChange = { cookingTime = it },
                        label = { Text(stringResource(R.string.cooking_time)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = servings,
                        onValueChange = { servings = it },
                        label = { Text(stringResource(R.string.servings)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Сложность
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.difficulty),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = getDifficultyText(difficulty),
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                Difficulty.values().forEach { diff ->
                                    DropdownMenuItem(
                                        text = { Text(getDifficultyText(diff)) },
                                        onClick = {
                                            difficulty = diff
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Категория
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.category),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = getCategoryText(category),
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                Category.values().forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(getCategoryText(cat)) },
                                        onClick = {
                                            category = cat
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.ingredients),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            itemsIndexed(ingredients) { index, ingredient ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = ingredient,
                        onValueChange = { newValue ->
                            ingredients = ingredients.toMutableList().apply {
                                this[index] = newValue
                            }
                        },
                        label = { Text("Ингредиент ${index + 1}") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        )
                    )
                    if (ingredients.size > 1) {
                        IconButton(
                            onClick = {
                                ingredients = ingredients.toMutableList().apply {
                                    removeAt(index)
                                }
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Удалить")
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        ingredients = ingredients.toMutableList().apply {
                            add("")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.add_ingredient))
                }
            }

            item {
                Text(
                    text = stringResource(R.string.instructions),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            itemsIndexed(instructions) { index, instruction ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    OutlinedTextField(
                        value = instruction,
                        onValueChange = { newValue ->
                            instructions = instructions.toMutableList().apply {
                                this[index] = newValue
                            }
                        },
                        label = { Text("Шаг ${index + 1}") },
                        modifier = Modifier.weight(1f),
                        minLines = 2,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Default
                        )
                    )
                    if (instructions.size > 1) {
                        IconButton(
                            onClick = {
                                instructions = instructions.toMutableList().apply {
                                    removeAt(index)
                                }
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Удалить")
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        instructions = instructions.toMutableList().apply {
                            add("")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.add_instruction))
                }
            }
        }
    }
}
//Валидация данных рецепта перед сохранением
private fun isValidRecipe(
    title: String,
    description: String,
    ingredients: List<String>,
    instructions: List<String>,
    cookingTime: String,
    servings: String
): Boolean {
    return title.isNotBlank() &&
            description.isNotBlank() &&
            ingredients.any { it.isNotBlank() } &&
            instructions.any { it.isNotBlank() } &&
            cookingTime.toIntOrNull() != null &&
            servings.toIntOrNull() != null
}

@Composable
private fun getDifficultyText(difficulty: Difficulty): String {
    return when (difficulty) {
        Difficulty.EASY -> stringResource(R.string.difficulty_easy)
        Difficulty.MEDIUM -> stringResource(R.string.difficulty_medium)
        Difficulty.HARD -> stringResource(R.string.difficulty_hard)
    }
}

@Composable
private fun getCategoryText(category: Category): String {
    return when (category) {
        Category.BREAKFAST -> stringResource(R.string.category_breakfast)
        Category.LUNCH -> stringResource(R.string.category_lunch)
        Category.DINNER -> stringResource(R.string.category_dinner)
        Category.DESSERT -> stringResource(R.string.category_dessert)
        Category.SNACK -> stringResource(R.string.category_snack)
        Category.BEVERAGE -> stringResource(R.string.category_beverage)
    }
}
