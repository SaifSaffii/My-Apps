package com.example.data

import kotlinx.coroutines.flow.Flow

class UserProfileRepository(private val dao: UserProfileDao) {
    val allProfiles: Flow<List<UserProfile>> = dao.getAllProfiles()

    suspend fun insert(profile: UserProfile): Long = dao.insertProfile(profile)

    suspend fun update(profile: UserProfile) = dao.updateProfile(profile)

    suspend fun delete(profile: UserProfile) = dao.deleteProfile(profile)

    suspend fun deleteById(id: Int) = dao.deleteProfileById(id)
}
