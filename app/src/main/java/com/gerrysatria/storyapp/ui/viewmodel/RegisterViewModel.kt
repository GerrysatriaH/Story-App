package com.gerrysatria.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.gerrysatria.storyapp.data.StoryRepository

class RegisterViewModel(private val repository: StoryRepository) : ViewModel(){
    fun register(name:String, email:String, password:String) = repository.register(name, email, password)
}