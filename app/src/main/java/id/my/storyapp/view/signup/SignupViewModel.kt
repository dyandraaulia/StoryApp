package id.my.storyapp.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import id.my.storyapp.data.repository.Repository
import id.my.storyapp.data.remote.Result
import id.my.storyapp.data.remote.model.RegisterModel
import id.my.storyapp.data.remote.response.RegisterResponse

class SignupViewModel(private val repository: Repository) : ViewModel() {
    fun postRegister(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> {
        val user = RegisterModel(name, email, password)
        return repository.postRegister(user)
    }
}