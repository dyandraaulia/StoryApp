package id.my.storyapp.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import id.my.storyapp.data.remote.Result
import id.my.storyapp.data.repository.Repository
import id.my.storyapp.data.remote.response.StoryResponse

class MapsViewModel(private val repository: Repository) : ViewModel() {
    fun getStoriesWithLocation(): LiveData<Result<StoryResponse>> =
        repository.getStoriesWithLocation()
}