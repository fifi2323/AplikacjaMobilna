package com.example.aplikacjemobilne.model

data class Cocktail(
    val name: String,
    val imageUrl: String,
    val instructions: String,
    val isFavorite: Boolean,
    val ingredients: List<String> = emptyList()
)