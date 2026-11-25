package com.example.warehouse.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.warehouse.data.DatabaseHelper
import com.example.warehouse.databinding.ActivityLoginBinding
import com.example.warehouse.ui.main.MainActivity
import com.example.warehouse.util.SessionManager
import com.example.warehouse.util.shake

/** Màn hình đăng nhập: kiểm tra tài khoản bằng truy vấn bảng users trong DB. */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper.getInstance(this)

        binding.btnLogin.setOnClickListener { attemptLogin() }
        binding.textGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val username = binding.editUsername.text?.toString()?.trim().orEmpty()
        val password = binding.editPassword.text?.toString()?.trim().orEmpty()

        if (username.isEmpty() || password.isEmpty()) {
            showError(getString(com.example.warehouse.R.string.error_login_empty))
            return
        }

        if (dbHelper.checkLogin(username, password)) {
            hideError()
            SessionManager.saveUsername(this, username)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            showError(getString(com.example.warehouse.R.string.error_login_wrong))
            binding.cardLogin.shake()
        }
    }

    private fun showError(message: String) {
        binding.textError.text = message
        binding.textError.visibility = android.view.View.VISIBLE
    }

    private fun hideError() {
        binding.textError.visibility = android.view.View.GONE
    }
}
