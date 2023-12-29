package id.my.storyapp.di

import android.content.Context
import id.my.storyapp.data.local.database.StoryDatabase
import id.my.storyapp.data.repository.Repository
import id.my.storyapp.data.remote.preferences.UserPreference
import id.my.storyapp.data.remote.preferences.dataStore
import id.my.storyapp.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): Repository {
        val storyDatabase = StoryDatabase.getDatabase(context)
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        return Repository.getInstance(storyDatabase, apiService, pref)
    }
}