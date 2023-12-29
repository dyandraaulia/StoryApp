package id.my.storyapp.view.signin

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import id.my.storyapp.R
import id.my.storyapp.data.remote.Result
import id.my.storyapp.data.remote.model.LoginModel
import id.my.storyapp.databinding.ActivitySigninBinding
import id.my.storyapp.view.ViewModelFactory
import id.my.storyapp.view.main.MainActivity
import id.my.storyapp.view.signup.SignupActivity

class SigninActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding
    private lateinit var signinViewModel: SigninViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        signinViewModel = obtainViewModel(this)

        setAnimation()
        onSigninBtnClicked()
        switchToSignup()
    }

    private fun setAnimation() {
        val signinTitle =
            ObjectAnimator.ofFloat(binding.tvSigninTitle, View.ALPHA, 1F).setDuration(300)
        val emailTitle =
            ObjectAnimator.ofFloat(binding.tvEmailTitle, View.ALPHA, 1F).setDuration(300)
        val email =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1F).setDuration(300)
        val passwordTitle =
            ObjectAnimator.ofFloat(binding.tvPasswordTitle, View.ALPHA, 1F).setDuration(300)
        val password =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1F).setDuration(300)
        val button = ObjectAnimator.ofFloat(binding.signinButton, View.ALPHA, 1F).setDuration(300)

        AnimatorSet().apply {
            playSequentially(signinTitle, emailTitle, email, passwordTitle, password, button)
            start()
        }
    }

    private fun onSigninBtnClicked() {
        binding.signinButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            signinViewModel.postLogin(email, password).observe(this) { result ->
                when (result) {
                    is Result.Success -> {
                        showLoading(false)
                        binding.signinStatus.text = getString(R.string.sign_in_success)
                        binding.signinStatus.setTextColor(getColor(R.color.signin_success))
                        signinViewModel.saveSession(
                            LoginModel(
                                email,
                                result.data.loginResult?.token.toString()
                            )
                        )
                        startActivity(Intent(this@SigninActivity, MainActivity::class.java))
                        finish()
                    }

                    is Result.Error -> {
                        showLoading(false)
                        binding.signinStatus.text = result.error
                        binding.signinStatus.setTextColor(getColor(R.color.error_color))
                    }

                    is Result.Empty -> {
                        showLoading(false)
                        Toast.makeText(
                            this,
                            getString(R.string.response_data_is_empty),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is Result.Loading -> {
                        showLoading(true)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun switchToSignup() {
        binding.switchToSignUp.setOnClickListener {
            val intentSignUp = Intent(this, SignupActivity::class.java)
            startActivity(intentSignUp)
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): SigninViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[SigninViewModel::class.java]
    }
}