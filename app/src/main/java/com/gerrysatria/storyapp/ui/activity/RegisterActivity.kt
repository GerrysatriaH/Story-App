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
import com.gerrysatria.storyapp.data.response.RegisterResponse
import com.gerrysatria.storyapp.databinding.ActivityRegisterBinding
import com.gerrysatria.storyapp.ui.viewmodel.RegisterViewModel
import com.gerrysatria.storyapp.ui.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        playAnimation()
        actionRegister()
        binding.tvToLogin.setOnClickListener{
            actionToLogin()
        }
    }

    private fun actionToLogin(){
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun actionRegister() {
        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            registerViewModel.register(name, email, password).observe(this@RegisterActivity){
                if (it != null) {
                    when(it) {
                        is Result.Loading -> {
                            showLoading(true)
                        }
                        is Result.Success -> {
                            processRegister(it.data)
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

    private fun processRegister(data: RegisterResponse){
        if (data.error){
            showToast(data.message)
        } else {
            showDialog(data.message)
        }
    }

    private fun playAnimation(){
        val tvRegister = ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 1f).setDuration(300)
        val tvMessage = ObjectAnimator.ofFloat(binding.tvRegisterMessage, View.ALPHA, 1f).setDuration(300)
        val tvName = ObjectAnimator.ofFloat(binding.tvRegisterName, View.ALPHA, 1f).setDuration(300)
        val edRegisterName = ObjectAnimator.ofFloat(binding.edRegisterNameLayout, View.ALPHA, 1f).setDuration(300)
        val tvEmail = ObjectAnimator.ofFloat(binding.tvRegisterEmail, View.ALPHA, 1f).setDuration(300)
        val edRegisterEmail = ObjectAnimator.ofFloat(binding.edRegisterEmailLayout, View.ALPHA, 1f).setDuration(300)
        val tvPassword = ObjectAnimator.ofFloat(binding.tvRegisterPassword, View.ALPHA, 1f).setDuration(300)
        val edRegisterPassword = ObjectAnimator.ofFloat(binding.edRegisterPasswordLayout, View.ALPHA, 1f).setDuration(300)
        val btnRegister = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(300)

        val tvAskToLogin = ObjectAnimator.ofFloat(binding.tvAskToLogin, View.ALPHA, 1f).setDuration(300)
        val tvToLogin = ObjectAnimator.ofFloat(binding.tvToLogin, View.ALPHA, 1f).setDuration(300)

        val together = AnimatorSet().apply {
            playTogether(tvAskToLogin, tvToLogin)
        }

        AnimatorSet().apply {
            playSequentially(
                tvRegister,
                tvMessage,
                tvName,
                edRegisterName,
                tvEmail,
                edRegisterEmail,
                tvPassword,
                edRegisterPassword,
                together,
                btnRegister
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
            actionToLogin()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showLoading(state: Boolean){
        binding.progressBarRegister.isVisible = state
    }
}