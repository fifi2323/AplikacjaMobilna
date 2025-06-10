package com.example.aplikacjemobilne.data
import androidx.room.*

@Entity(tableName = "cocktails")
data class CocktailDB(
    @PrimaryKey val name: String,   // klucz naturalny
    val imageUrl: String,
    val instructions: String,
    val ingredients: String,        // np. "50ml Rum;Soda;Mięta"
    val isFavourite: Boolean = true
)