package id.my.storyapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.Gson
import id.my.storyapp.data.local.database.StoryDatabase
import id.my.storyapp.data.remote.Result
import id.my.storyapp.data.remote.preferences.UserPreference
import id.my.storyapp.data.remote.model.LoginModel
import id.my.storyapp.data.remote.model.RegisterModel
import id.my.storyapp.data.remote.response.ErrorResponse
import id.my.storyapp.data.remote.response.FileUploadResponse
import id.my.storyapp.data.remote.response.ListStoryItem
import id.my.storyapp.data.remote.response.LoginResponse
import id.my.storyapp.data.remote.response.RegisterResponse
import id.my.storyapp.data.remote.response.StoryResponse
import id.my.storyapp.data.remote.retrofit.ApiService
import id.my.storyapp.data.paging.StoryRemoteMediator
import id.my.storyapp.util.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class Repository private constructor(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    private val userPreference: UserPreference,
) {
    fun postRegister(registerData: RegisterModel): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response =
                apiService.register(registerData.name, registerData.email, registerData.password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            Log.d(Repository::class.java.simpleName, "postRegister: ${e.message.toString()}")
            emit(Result.Error(errorMessage.toString()))
        }
    }

    fun postLogin(loginData: RegisterModel): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        wrapEspressoIdlingResource {
            try {
                val response = apiService.login(loginData.email, loginData.password)
                emit(Result.Success(response))
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val errorMessage = errorBody.message
                Log.d(Repository::class.java.simpleName, "postRegister: ${e.message.toString()}")
                emit(Result.Error(errorMessage.toString()))
            }
        }
    }

    fun postStory(
        multipartBody: MultipartBody.Part,
        requestBody: RequestBody,
        lat: Float?,
        lon: Float?
    ): LiveData<Result<FileUploadResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.uploadImage(multipartBody, requestBody, lat, lon)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            Log.d(Repository::class.java.simpleName, "postRegister: ${e.message.toString()}")
            emit(Result.Error(errorMessage.toString()))
        }
    }

    suspend fun saveSession(user: LoginModel) = userPreference.saveSession(user)

    @OptIn(ExperimentalPagingApi::class)
    fun getStoriesWithPaging(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 6
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun getStoriesWithLocation(): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoriesWithLocation()
            if (response.listStory.isNotEmpty()) {
                emit(Result.Success(response))
            } else {
                emit(Result.Empty)
            }
        } catch (e: Exception) {
            Log.d(Repository::class.java.simpleName, "getStories: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getSession(): Flow<LoginModel> = userPreference.getSession()

    suspend fun logout() {
        wrapEspressoIdlingResource {
            userPreference.logout()
        }
    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            storyDatabase: StoryDatabase,
            apiService: ApiService,
            userPreference: UserPreference,
        ): Repository = instance ?: synchronized(this) {
            instance ?: Repository(storyDatabase, apiService, userPreference)
        }.also { instance = it }
    }
}