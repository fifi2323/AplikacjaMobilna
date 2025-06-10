package com.example.aplikacjemobilne.data
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CocktailDao {
    @Query("SELECT * FROM cocktails")
    fun getAllCocktails(): LiveData<List<CocktailDB>>

    @Query("SELECT * FROM cocktails WHERE name = :name LIMIT 1")
    suspend fun getCocktailByName(name: String): CocktailDB?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCocktail(cocktail: CocktailDB)

    @Update
    suspend fun updateCocktail(cocktail: CocktailDB)

    @Delete
    suspend fun deleteCocktail(cocktail: CocktailDB)


}