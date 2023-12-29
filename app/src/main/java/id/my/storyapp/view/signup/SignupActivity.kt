package id.my.storyapp.view.signup

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
import id.my.storyapp.databinding.ActivitySignupBinding
import id.my.storyapp.view.ViewModelFactory
import id.my.storyapp.view.signin.SigninActivity

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var signupViewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        signupViewModel = obtainViewModel(this@SignupActivity)

        onSignupBtnClicked()
        binding.switchToSignIn.setOnClickListener {
            switchToSignin()
        }
        setAnimation()
    }

    private fun setAnimation() {
        val signupTitle =
            ObjectAnimator.ofFloat(binding.tvSignupTitle, View.ALPHA, 1F).setDuration(300)
        val nameTitle = ObjectAnimator.ofFloat(binding.tvNameTitle, View.ALPHA, 1F).setDuration(300)
        val name =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1F).setDuration(300)
        val emailTitle =
            ObjectAnimator.ofFloat(binding.tvEmailTitle, View.ALPHA, 1F).setDuration(300)
        val email =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1F).setDuration(300)
        val passwordTitle =
            ObjectAnimator.ofFloat(binding.tvPasswordTitle, View.ALPHA, 1F).setDuration(300)
        val password =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1F).setDuration(300)
        val button = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1F).setDuration(300)

        AnimatorSet().apply {
            playSequentially(
                signupTitle,
                nameTitle,
                name,
                emailTitle,
                email,
                passwordTitle,
                password,
                button
            )
            start()
        }
    }

    private fun onSignupBtnClicked() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            signupViewModel.postRegister(name, email, password).observe(this) { result ->
                when (result) {
                    is Result.Success -> {
                        showLoading(false)
                        Toast.makeText(
                            this@SignupActivity,
                            getString(R.string.sign_up_success), Toast.LENGTH_SHORT
                        ).show()
                        switchToSignin()
                    }

                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(this@SignupActivity, result.error, Toast.LENGTH_SHORT).show()
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

    private fun switchToSignin() {
        val intentSignIn = Intent(this, SigninActivity::class.java)
        intentSignIn.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intentSignIn)
    }

    private fun obtainViewModel(activity: AppCompatActivity): SignupViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[SignupViewModel::class.java]
    }
}