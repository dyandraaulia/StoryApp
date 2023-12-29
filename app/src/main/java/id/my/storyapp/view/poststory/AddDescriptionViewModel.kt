package id.my.storyapp.view.poststory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import id.my.storyapp.data.repository.Repository
import id.my.storyapp.data.remote.Result
import id.my.storyapp.data.remote.response.FileUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddDescriptionViewModel(private val repository: Repository) : ViewModel() {
    fun postStory(
        multipartBody: MultipartBody.Part,
        requestBody: RequestBody,
        lat: Float?,
        lon: Float?
    ): LiveData<Result<FileUploadResponse>> =
        repository.postStory(multipartBody, requestBody, lat, lon)
}