package com.gerrysatria.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.gerrysatria.storyapp.data.StoryRepository
import com.gerrysatria.storyapp.data.datastore.SessionPreferences
import com.gerrysatria.storyapp.data.database.StoryDatabase
import com.gerrysatria.storyapp.data.service.ApiConfig

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val preferences = SessionPreferences.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        val database = StoryDatabase.getInstance(context)
        return StoryRepository.getInstance(apiService, preferences, database)
    }
}