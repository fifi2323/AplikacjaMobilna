package com.example.aplikacjemobilne.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aplikacjemobilne.model.Cocktail


/**
 * Główna klasa bazy danych Room.
 * Zawiera encję CocktailDB i wersję schematu = 2.
 */
@Database(
    entities = [CocktailDB::class],
    version = 3,
    exportSchema = false   // możesz ustawić true, jeśli chcesz versioned schema
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cocktailDao(): CocktailDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "cocktail_db"
            )
                .fallbackToDestructiveMigration() // usuwa i odtwarza bazę przy zmianie wersji
                .build()
    }
}
