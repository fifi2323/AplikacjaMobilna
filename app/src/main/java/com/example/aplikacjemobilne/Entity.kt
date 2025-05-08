package com.example.aplikacjemobilne
import androidx.room.*

@Entity
data class CocktailDB(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    val name: String,
    val isFavourite: Boolean,
    val imageResId: Int? = null  // Make this nullable
)