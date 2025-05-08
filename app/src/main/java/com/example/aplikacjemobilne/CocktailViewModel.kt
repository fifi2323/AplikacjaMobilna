package com.example.aplikacjemobilne;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CocktailViewModel(application: Application) : AndroidViewModel(application) {
    private val cocktailDao = AppDatabase.getInstance(application).cocktailDao()

    val allCocktails: LiveData<List<CocktailDB>> = cocktailDao.getAllCocktails()

    fun addCocktail(name: String, isFavourite: Boolean, imageResId: Int? = null) {
        viewModelScope.launch {
            cocktailDao.insertCocktail(CocktailDB(
                name = name,
                isFavourite = isFavourite,
                imageResId = imageResId
            ))
        }
    }

    fun updateCocktail(cocktail: CocktailDB) {
        viewModelScope.launch {
            cocktailDao.updateCocktail(cocktail)
        }
    }

    fun deleteCocktail(cocktail: CocktailDB) {
        viewModelScope.launch {
            cocktailDao.deleteCocktail(cocktail)
        }
    }
}
