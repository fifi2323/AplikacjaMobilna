package com.example.aplikacjemobilne.mapper

import com.example.aplikacjemobilne.model.Cocktail
import com.example.aplikacjemobilne.data.CocktailDB
import com.example.aplikacjemobilne.model.Drink

// Drink (API) → CocktailDB (Room)
fun Drink.toCocktailDB(): CocktailDB {
    val ingredientsList = listOfNotNull(
        com.example.aplikacjemobilne.formatIngredient(strIngredient1, strMeasure1),
        com.example.aplikacjemobilne.formatIngredient(strIngredient2, strMeasure2),
        com.example.aplikacjemobilne.formatIngredient(strIngredient3, strMeasure3),
        com.example.aplikacjemobilne.formatIngredient(strIngredient4, strMeasure4),
        com.example.aplikacjemobilne.formatIngredient(strIngredient5, strMeasure5),
        com.example.aplikacjemobilne.formatIngredient(strIngredient6, strMeasure6),
        com.example.aplikacjemobilne.formatIngredient(strIngredient7, strMeasure7),
        com.example.aplikacjemobilne.formatIngredient(strIngredient8, strMeasure8),
        com.example.aplikacjemobilne.formatIngredient(strIngredient9, strMeasure9),
        com.example.aplikacjemobilne.formatIngredient(strIngredient10, strMeasure10),
        com.example.aplikacjemobilne.formatIngredient(strIngredient11, strMeasure11),
        com.example.aplikacjemobilne.formatIngredient(strIngredient12, strMeasure12),
        com.example.aplikacjemobilne.formatIngredient(strIngredient13, strMeasure13),
        com.example.aplikacjemobilne.formatIngredient(strIngredient14, strMeasure14),
        com.example.aplikacjemobilne.formatIngredient(strIngredient15, strMeasure15)
    )
    return CocktailDB(
        name         = strDrink.orEmpty(),
        imageUrl     = strDrinkThumb.orEmpty(),
        instructions = strInstructions.orEmpty(),
        ingredients  = ingredientsList.joinToString(";"),
        isFavourite  = true
    )
}

// CocktailDB (Room) → Cocktail (UI)
fun CocktailDB.toCocktail(): Cocktail =
    Cocktail(
        name         = name,
        imageUrl     = imageUrl,
        instructions = instructions,
        isFavorite   = isFavourite,
        ingredients  = if (ingredients.isBlank()) emptyList()
        else ingredients.split(";")
    )

// Cocktail (UI) → CocktailDB (Room), jeżeli chcesz zapisać ulubione z UI
fun Cocktail.toCocktailDB(): CocktailDB =
    CocktailDB(
        name         = name,
        imageUrl     = imageUrl,
        instructions = instructions,
        ingredients  = ingredients.joinToString(";"),
        isFavourite  = isFavorite
    )

// Helper
private fun formatIngredient(ingredient: String?, measure: String?): String? {
    if (ingredient.isNullOrBlank()) return null
    return "${measure.orEmpty().trim()} ${ingredient.trim()}".trim()
}