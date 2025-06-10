package com.example.aplikacjemobilne.retrofit

import com.example.aplikacjemobilne.model.ListResponse
import com.squareup.moshi.Moshi
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


/** The RetrofitClient object is a singleton class that creates a
 * service instance to access the API
 */
object RetrofitClient {
    // Base URL for the API
    private const val BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/"

    val apiServiceInstance: ApiService by lazy {
        // Create a Retrofit instance using the getClient() method to access the API
        // via the ApiService interface
        getClient().create(ApiService::class.java)
    }

    // Function to build and return a Retrofit instance
    private fun getClient(): Retrofit {
        // Create a Moshi instance for JSON parsing KotlinJsonAdapterFactory is used to
        // automatically generate the adapter for the data classes
        val moshi = Moshi.Builder().build()
        // Create a Retrofit instance with the BASE_URL and Moshi converter
        // using the builder pattern
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        // Return the Retrofit instance
        return retrofit
    }
}

interface ApiService {

    // Przykład: https://www.thecocktaildb.com/api/json/v1/1/search.php?f=a
    @GET("search.php")
    fun searchDrinksByFirstLetter(
        @Query("f") firstLetter: String
    ): Call<ListResponse>

    // Przykład: https://www.thecocktaildb.com/api/json/v1/1/search.php?s=margarita
    @GET("search.php")
    fun searchDrinksByName(
        @Query("s") name: String
    ): Call<ListResponse>

    // Przykład: https://www.thecocktaildb.com/api/json/v1/1/filter.php?c=Cocktail
    @GET("filter.php")
    fun filterByCategory(
        @Query("c") category: String
    ): Call<ListResponse>

    // Przykład: https://www.thecocktaildb.com/api/json/v1/1/list.php?c=list
    @GET("list.php")
    fun listCategories(
        @Query("c") value: String = "list"
    ): Call<ListResponse>
}