package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [UserProfile::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "age_converter_database"
                )
                .addCallback(DatabaseCallback())
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.userProfileDao())
                    }
                }
            }

            suspend fun populateInitialData(dao: UserProfileDao) {
                // Seed a default profile: Myself (born in 2000, e.g. Jan 1)
                dao.insertProfile(
                    UserProfile(
                        name = "Myself",
                        birthYear = 2000,
                        birthMonth = 1,
                        birthDay = 1,
                        emoji = "👤",
                        isDefault = true
                    )
                )
                dao.insertProfile(
                    UserProfile(
                        name = "My Dog 🐶",
                        birthYear = 2021,
                        birthMonth = 6,
                        birthDay = 15,
                        emoji = "🐶",
                        isPet = true,
                        petType = "DOG_MEDIUM"
                    )
                )
            }
        }
    }
}
