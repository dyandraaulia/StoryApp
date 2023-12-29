package id.my.storyapp.data.remote.model

data class LoginModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)
