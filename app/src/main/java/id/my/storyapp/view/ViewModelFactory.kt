package id.my.storyapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.my.storyapp.data.repository.Repository
import id.my.storyapp.di.Injection
import id.my.storyapp.view.main.MainViewModel
import id.my.storyapp.view.maps.MapsViewModel
import id.my.storyapp.view.poststory.AddDescriptionViewModel
import id.my.storyapp.view.signin.SigninViewModel
import id.my.storyapp.view.signup.SignupViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory private constructor(private val repository: Repository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
            return SignupViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(SigninViewModel::class.java)) {
            return SigninViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(AddDescriptionViewModel::class.java)) {
            return AddDescriptionViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
        }
    }
}