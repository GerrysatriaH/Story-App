package com.gerrysatria.storyapp.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    fun getSession(): Flow<Session> {
        val session = dataStore.data.map { preference ->
            Session(
                preference[USERID_KEY] ?: "",
                preference[NAME_KEY] ?: "",
                preference[TOKEN_KEY] ?: ""
            )
        }
        return session
    }

    suspend fun saveSession(data : Session){
        dataStore.edit { preference ->
            preference[USERID_KEY] = data.userId
            preference[NAME_KEY] = data.name
            preference[TOKEN_KEY] = data.token
        }
    }

    suspend fun logout(){
        dataStore.edit { preference ->
            preference.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SessionPreferences? = null
        private val USERID_KEY = stringPreferencesKey("userid")
        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")

        fun getInstance(dataStore: DataStore<Preferences>): SessionPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SessionPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}