package com.gerrysatria.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.gerrysatria.storyapp.data.StoryRepository
import java.io.File

class AddStoryViewModel (private val repository: StoryRepository) : ViewModel() {
    fun uploadStory(file: File, description: String, lat: Double?, lon: Double?) = repository.uploadStory(file, description, lat, lon)
}