package com.gerrysatria.storyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gerrysatria.storyapp.data.StoryRepository
import com.gerrysatria.storyapp.data.datastore.Session
import com.gerrysatria.storyapp.data.database.StoryEntity
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository): ViewModel(){
    fun getSession(): LiveData<Session> = repository.getSession()
    fun getPhotoStory() = repository.getPhotoStory()

    fun getStories(): LiveData<PagingData<StoryEntity>> = repository.getStories().cachedIn(viewModelScope)

    fun logout() = viewModelScope.launch {
        repository.logout()
    }
}