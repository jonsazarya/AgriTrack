package com.example.agritrack.view.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.agritrack.MainActivity
import com.example.agritrack.R
import com.example.agritrack.databinding.ActivityLoginBinding
import com.example.agritrack.di.Result
import com.example.agritrack.view.ViewModelFactory
import com.example.agritrack.view.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.getSession().observe(this) {
            if (it.isLogin) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }

        binding.tvRegister.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty()) {
                binding.etEmail.error = "Please enter your email"
            } else if(password.isEmpty()) {
                binding.etPassword.error = "Please enter your password"
            } else {
                viewModel.login(email, password).observe(this) {
                    if (it != null) {
                        when (it) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                                binding.btnLogin.isEnabled = false
                            }
                            is Result.Success -> {
                                binding.progressBar.visibility = View.GONE
                                binding.btnLogin.isEnabled = true

                                val response = it.data
                                val token = response.token.toString()
                                val role = response.role.toString()

                                viewModel.saveSession(token, role)

                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            is Result.Error -> {
                                binding.progressBar.visibility = View.GONE
                                binding.btnLogin.isEnabled = true

                                AlertDialog.Builder(this).apply {
                                    setTitle(it.error)
                                    setPositiveButton("Ok") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    create()
                                    show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}