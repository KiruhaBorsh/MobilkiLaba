package com.example.culinarium.data

import java.util.UUID

data class Recipe(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val cookingTime: Int, // в минутах
    val servings: Int,
    val difficulty: Difficulty,
    val category: Category,
    val imageUrl: String? = null,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class Difficulty {
    EASY, MEDIUM, HARD
}

enum class Category {
    BREAKFAST, LUNCH, DINNER, DESSERT, SNACK, BEVERAGE
}

