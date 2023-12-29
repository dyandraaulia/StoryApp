package id.my.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import id.my.storyapp.data.repository.Repository
import id.my.storyapp.data.remote.model.LoginModel
import id.my.storyapp.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {
    val stories: LiveData<PagingData<ListStoryItem>> =
        repository.getStoriesWithPaging().cachedIn(viewModelScope)

    fun getSession(): LiveData<LoginModel> = repository.getSession().asLiveData()

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}