package com.gerrysatria.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gerrysatria.storyapp.data.StoryRepository
import com.gerrysatria.storyapp.data.datastore.Session
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: StoryRepository) : ViewModel() {
    fun login(email:String, password:String) = repository.login(email, password)

    fun saveSession(session: Session) = viewModelScope.launch {
        repository.saveSession(session)
    }
}