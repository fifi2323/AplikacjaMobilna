package com.example.aplikacjemobilne

import android.app.Application
import androidx.lifecycle.*
import androidx.room.PrimaryKey
import com.example.aplikacjemobilne.data.AppDatabase
import com.example.aplikacjemobilne.model.Cocktail
import com.example.aplikacjemobilne.data.CocktailDB
import com.example.aplikacjemobilne.mapper.toCocktailDB
import com.example.aplikacjemobilne.model.Drink
import com.example.aplikacjemobilne.model.ListResponse
import com.example.aplikacjemobilne.retrofit.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CocktailViewModel(application: Application) : AndroidViewModel(application) {

    private val cocktailDao = AppDatabase.getInstance(application).cocktailDao()

    // Dane z lokalnej bazy danych
    val favouriteDB: LiveData<List<CocktailDB>> = cocktailDao.getAllCocktails()

    // 🔽 Dane z API
    private val _apiCocktails = MutableLiveData<List<Drink>>()
    val apiCocktails: LiveData<List<Drink>> get() = _apiCocktails

    // Dodawanie do bazy
    fun addCocktail(name: String, isFavourite: Boolean, imageResId: Int? = null) {
        viewModelScope.launch {
            cocktailDao.insertCocktail(
                CocktailDB(
                    name = name,   // klucz naturalny
                    imageUrl= "",
                    instructions = "",
                    ingredients = "",        // np. "50ml Rum;Soda;Mięta"
                    isFavourite = true
                )
            )
        }
    }

    fun toggleFavorite(cocktail: Cocktail) = viewModelScope.launch {
        val existing = cocktailDao.getCocktailByName(cocktail.name)
        if (existing != null) {
            cocktailDao.deleteCocktail(existing)
        } else {
            cocktailDao.insertCocktail(
                CocktailDB(
                    name = cocktail.name,
                    imageUrl = cocktail.imageUrl,
                    instructions = cocktail.instructions,
                    ingredients = cocktail.ingredients.joinToString(";"),
                    isFavourite = true
                )
            )
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

    // 🔄 Pobieranie drinków z API po literze
    fun fetchCocktailsByFirstLetter(letter: String = "a") {
        RetrofitClient.apiServiceInstance.searchDrinksByFirstLetter(letter)
            .enqueue(object : Callback<ListResponse> {
                override fun onResponse(call: Call<ListResponse>, response: Response<ListResponse>) {
                    if (response.isSuccessful) {
                        _apiCocktails.value = response.body()?.drinks ?: emptyList()
                    } else {
                        _apiCocktails.value = emptyList()
                    }
                }

                override fun onFailure(call: Call<ListResponse>, t: Throwable) {
                    // Możesz dodać logowanie błędu, np. Log.e(...)
                    _apiCocktails.value = emptyList()
                }
            })
    }

    // 🔍 Pobieranie drinków po nazwie
    fun fetchCocktailsByName(name: String) {
        RetrofitClient.apiServiceInstance.searchDrinksByName(name)
            .enqueue(object : Callback<ListResponse> {
                override fun onResponse(call: Call<ListResponse>, response: Response<ListResponse>) {
                    if (response.isSuccessful) {
                        _apiCocktails.value = response.body()?.drinks ?: emptyList()
                    } else {
                        _apiCocktails.value = emptyList()
                    }
                }

                override fun onFailure(call: Call<ListResponse>, t: Throwable) {
                    _apiCocktails.value = emptyList()
                }
            })
    }


}
