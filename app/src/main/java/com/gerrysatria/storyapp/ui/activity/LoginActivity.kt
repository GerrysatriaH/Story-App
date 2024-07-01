package com.gerrysatria.storyapp.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.gerrysatria.storyapp.R
import com.gerrysatria.storyapp.data.Result
import com.gerrysatria.storyapp.data.datastore.Session
import com.gerrysatria.storyapp.data.response.LoginResponse
import com.gerrysatria.storyapp.databinding.ActivityLoginBinding
import com.gerrysatria.storyapp.ui.viewmodel.LoginViewModel
import com.gerrysatria.storyapp.ui.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        playAnimation()
        actionToRegister()
        actionLogin()
    }

    private fun actionLogin(){
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            loginViewModel.login(email, password).observe(this@LoginActivity){
                if (it != null) {
                    when(it) {
                        is Result.Loading -> {
                            showLoading(true)
                        }
                        is Result.Success -> {
                            processLogin(it.data)
                            showLoading(false)
                        }
                        is Result.Error -> {
                            showToast(it.error)
                            showLoading(false)
                        }
                    }
                }
            }
        }
    }

    private fun actionToRegister(){
        binding.tvToRegister.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun processLogin(data: LoginResponse){
        if (data.error){
            showToast(data.message)
        } else {
            loginViewModel.saveSession(
                Session(
                    data.loginResult.userId,
                    data.loginResult.name,
                    AUTH_KEY + data.loginResult.token
                )
            )
            showDialog(data.message)
        }
    }

    private fun toMainActivity(){
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun playAnimation(){
        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val tvLogin = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 1f).setDuration(300)
        val tvMessage = ObjectAnimator.ofFloat(binding.tvMessage, View.ALPHA, 1f).setDuration(300)
        val tvEmail = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(300)
        val edLoginEmail = ObjectAnimator.ofFloat(binding.edLoginEmailLayout, View.ALPHA, 1f).setDuration(300)
        val tvPassword = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(300)
        val edLoginPassword = ObjectAnimator.ofFloat(binding.edLoginPasswordLayout, View.ALPHA, 1f).setDuration(300)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(300)

        val tvAskToRegister = ObjectAnimator.ofFloat(binding.tvAskToRegister, View.ALPHA, 1f).setDuration(300)
        val tvToRegister = ObjectAnimator.ofFloat(binding.tvToRegister, View.ALPHA, 1f).setDuration(300)

        val together = AnimatorSet().apply {
            playTogether(tvAskToRegister, tvToRegister)
        }

        AnimatorSet().apply {
            playSequentially(
                tvLogin,
                tvMessage,
                tvEmail,
                edLoginEmail,
                tvPassword,
                edLoginPassword,
                together,
                btnLogin
            )
            startDelay = 100
        }.start()
    }

    private fun showToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showDialog(message: String){
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton(getString(R.string.positive_button)) { _, _ ->
            toMainActivity()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showLoading(state: Boolean){
        binding.progressBarLogin.isVisible = state
    }

    companion object{
        private const val AUTH_KEY = "Bearer "
    }
}