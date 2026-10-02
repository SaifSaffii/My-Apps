package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val birthYear: Int,
    val birthMonth: Int, // 1 to 12
    val birthDay: Int,   // 1 to 31
    val birthHour: Int = 0,
    val birthMinute: Int = 0,
    val emoji: String = "👤",
    val isPet: Boolean = false,
    val petType: String = "HUMAN", // "HUMAN", "DOG_SMALL", "DOG_MEDIUM", "DOG_LARGE", "CAT", "HORSE", "RABBIT", "HAMSTER"
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
