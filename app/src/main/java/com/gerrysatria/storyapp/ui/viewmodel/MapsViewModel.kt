package com.gerrysatria.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.gerrysatria.storyapp.data.StoryRepository

class MapsViewModel(private val repository: StoryRepository): ViewModel() {
    fun getStoryWithLocation() = repository.getStoryWithLocation()
}