package com.example.aplikacjemobilne

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CocktailDB::class], version = 2)  // Increment version
abstract class AppDatabase : RoomDatabase() {
    abstract fun cocktailDao(): CocktailDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cocktail_db"
                )
                    .fallbackToDestructiveMigration()  // For development only
                    .build()
                    .also { INSTANCE = it }
            }
    }
}