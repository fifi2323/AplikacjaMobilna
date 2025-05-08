package com.example.aplikacjemobilne
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CocktailDao {
    @Query("SELECT * FROM CocktailDB")
    fun getAllCocktails(): LiveData<List<CocktailDB>>

    @Insert
    suspend fun insertCocktail(cocktail: CocktailDB)

    @Update
    suspend fun updateCocktail(cocktail: CocktailDB)

    @Delete
    suspend fun deleteCocktail(cocktail: CocktailDB)
}