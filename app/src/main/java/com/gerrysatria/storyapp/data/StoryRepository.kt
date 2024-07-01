package com.gerrysatria.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.gerrysatria.storyapp.data.datastore.Session
import com.gerrysatria.storyapp.data.datastore.SessionPreferences
import com.gerrysatria.storyapp.data.database.StoryDatabase
import com.gerrysatria.storyapp.data.database.StoryEntity
import com.gerrysatria.storyapp.data.paging.StoryRemoteMediator
import com.gerrysatria.storyapp.data.response.AddStoryResponse
import com.gerrysatria.storyapp.data.response.LoginResponse
import com.gerrysatria.storyapp.data.response.RegisterResponse
import com.gerrysatria.storyapp.data.response.StoriesResponse
import com.gerrysatria.storyapp.data.service.ApiService
import com.gerrysatria.storyapp.utils.wrapEspressoIdlingResource
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository (
    private val apiService: ApiService,
    private val preferences: SessionPreferences,
    private val storyDatabase: StoryDatabase
){

    fun register(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.register(name, email, password)
                emit(Result.Success(response))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
                emit(Result.Error(errorResponse.message))
            }
        }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> =
        liveData {
            emit(Result.Loading)
            wrapEspressoIdlingResource {
                try {
                    val response = apiService.login(email, password)
                    emit(Result.Success(response))
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
                    emit(Result.Error(errorResponse.message))
                }
            }
        }

    fun getPhotoStory(): List<String> = storyDatabase.storyDao().getPhotoUrl()

    fun getSession() : LiveData<Session> = preferences.getSession().asLiveData()

    suspend fun saveSession(session : Session) = preferences.saveSession(session)

    suspend fun logout() = preferences.logout()

    fun getStories() : LiveData<PagingData<StoryEntity>>{
        wrapEspressoIdlingResource {
            @OptIn(ExperimentalPagingApi::class)
            return Pager (
                config = PagingConfig(
                    pageSize = 5
                ),
                remoteMediator = StoryRemoteMediator(preferences,storyDatabase, apiService),
                pagingSourceFactory = {
                    storyDatabase.storyDao().getAllStory()
                }
            ).liveData
        }
    }

    fun getStoryWithLocation() : LiveData<Result<StoriesResponse>> =
        liveData {
            emit(Result.Loading)
            try{
                val token = preferences.getSession().first().token
                val response = apiService.getStoriesWithLocation(token)
                emit(Result.Success(response))
            } catch (e: HttpException){
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, StoriesResponse::class.java)
                emit(Result.Error(errorResponse.message))
            }
        }

    fun uploadStory(imageFile: File, description: String, lat: Double?, lon: Double?) =
        liveData {
            emit(Result.Loading)
            wrapEspressoIdlingResource {
                val token = preferences.getSession().first().token
                val requestBody = description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile
                )
                try {
                    val successResponse = apiService.uploadStory(
                        token,
                        multipartBody,
                        requestBody,
                        lat,
                        lon
                    )
                    emit(Result.Success(successResponse))
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, AddStoryResponse::class.java)
                    emit(Result.Error(errorResponse.message))
                }
            }
        }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            preferences: SessionPreferences,
            storyDatabase: StoryDatabase
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, preferences, storyDatabase)
            }.also { instance = it }
    }
}