package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.loginwithanimation.view.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var viewModel: SignupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()

        lifecycleScope.launch {
            val factory = ViewModelFactory.getInstance(this@SignupActivity)
            viewModel = ViewModelProvider(this@SignupActivity, factory)[SignupViewModel::class.java]

            observeViewModel()
        }
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showMessage("Oops!", getString(R.string.error_form))
                return@setOnClickListener
            }

            if (password.length >= 8) {
                viewModel.register(name, email, password)
            } else {
                showMessage("Oops!", getString(R.string.password_confirm))
            }
        }
    }

    private fun showMessage(title: String, message: String) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { _, _ ->
                if (title == "Yeah!") {
                    finish()
                }
            }
            create()
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.registerResponse.observe(this) { response ->
            viewModel.setLoading(false)
            if (response.error == false) {
                showMessage("Yeah!", "Account with ${binding.edRegisterEmail.text} has been created. Please log in to see your stories.")
            } else {
                showMessage("Oops!", response.message ?: "Registration failed")
            }
        }
    }
}