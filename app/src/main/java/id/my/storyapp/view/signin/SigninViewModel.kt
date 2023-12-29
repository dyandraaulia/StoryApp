package id.my.storyapp.view.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.my.storyapp.data.repository.Repository
import id.my.storyapp.data.remote.Result
import id.my.storyapp.data.remote.model.LoginModel
import id.my.storyapp.data.remote.model.RegisterModel
import id.my.storyapp.data.remote.response.LoginResponse
import kotlinx.coroutines.launch

class SigninViewModel(private val repository: Repository) : ViewModel() {
    fun postLogin(email: String, password: String): LiveData<Result<LoginResponse>> {
        val user = RegisterModel("", email, password)
        return repository.postLogin(user)
    }

    fun saveSession(user: LoginModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}