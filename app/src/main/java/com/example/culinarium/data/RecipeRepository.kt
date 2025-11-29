package com.example.culinarium.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecipeRepository {
    private val _recipes = MutableStateFlow<List<Recipe>>(getSampleRecipes())
    val recipes: Flow<List<Recipe>> = _recipes.asStateFlow()

    fun addRecipe(recipe: Recipe) {
        val currentRecipes = _recipes.value.toMutableList()
        currentRecipes.add(recipe)
        _recipes.value = currentRecipes
    }

    fun updateRecipe(recipe: Recipe) {
        val currentRecipes = _recipes.value.toMutableList()
        val index = currentRecipes.indexOfFirst { it.id == recipe.id }
        if (index != -1) {
            currentRecipes[index] = recipe
            _recipes.value = currentRecipes
        }
    }

    fun deleteRecipe(recipeId: String) {
        val currentRecipes = _recipes.value.toMutableList()
        currentRecipes.removeAll { it.id == recipeId }
        _recipes.value = currentRecipes
    }

    fun getRecipeById(id: String): Recipe? {
        return _recipes.value.find { it.id == id }
    }

    fun toggleFavorite(recipeId: String) {
        val currentRecipes = _recipes.value.toMutableList()
        val index = currentRecipes.indexOfFirst { it.id == recipeId }
        if (index != -1) {
            val recipe = currentRecipes[index]
            currentRecipes[index] = recipe.copy(isFavorite = !recipe.isFavorite)
            _recipes.value = currentRecipes
        }
    }

    private fun getSampleRecipes(): List<Recipe> {
        return listOf(
            Recipe(
                title = "Борщ украинский",
                description = "Классический украинский борщ с говядиной и свеклой",
                ingredients = listOf(
                    "Говядина - 500г",
                    "Свекла - 2 шт",
                    "Капуста - 300г",
                    "Морковь - 1 шт",
                    "Лук - 1 шт",
                    "Картофель - 3 шт",
                    "Томатная паста - 2 ст.л.",
                    "Чеснок - 3 зубчика",
                    "Зелень - по вкусу"
                ),
                instructions = listOf(
                    "Сварить мясной бульон",
                    "Натереть свеклу и морковь",
                    "Обжарить лук с томатной пастой",
                    "Добавить овощи в бульон",
                    "Варить 30 минут",
                    "Подавать со сметаной"
                ),
                cookingTime = 120,
                servings = 6,
                difficulty = Difficulty.MEDIUM,
                category = Category.LUNCH
            ),
            Recipe(
                title = "Омлет с сыром",
                description = "Простой и вкусный омлет на завтрак",
                ingredients = listOf(
                    "Яйца - 3 шт",
                    "Молоко - 50мл",
                    "Сыр - 50г",
                    "Соль - по вкусу",
                    "Масло сливочное - 1 ст.л."
                ),
                instructions = listOf(
                    "Взбить яйца с молоком и солью",
                    "Натереть сыр",
                    "Разогреть сковороду с маслом",
                    "Вылить яичную смесь",
                    "Добавить сыр",
                    "Готовить 3-5 минут"
                ),
                cookingTime = 10,
                servings = 2,
                difficulty = Difficulty.EASY,
                category = Category.BREAKFAST
            ),
            Recipe(
                title = "Тирамису",
                description = "Итальянский десерт с кофе и маскарпоне",
                ingredients = listOf(
                    "Маскарпоне - 500г",
                    "Яйца - 4 шт",
                    "Сахар - 100г",
                    "Кофе эспрессо - 200мл",
                    "Печенье савоярди - 200г",
                    "Какао - 2 ст.л.",
                    "Ликер - 2 ст.л."
                ),
                instructions = listOf(
                    "Взбить желтки с сахаром",
                    "Добавить маскарпоне",
                    "Взбить белки отдельно",
                    "Смешать с кремом",
                    "Обмакнуть печенье в кофе",
                    "Выложить слоями",
                    "Охладить 4 часа"
                ),
                cookingTime = 30,
                servings = 8,
                difficulty = Difficulty.HARD,
                category = Category.DESSERT
            )
        )
    }
}

