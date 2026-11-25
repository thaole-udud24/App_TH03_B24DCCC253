package com.example.warehouse.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.warehouse.R
import com.example.warehouse.data.DatabaseHelper
import com.example.warehouse.databinding.ActivityRegisterBinding
import com.example.warehouse.util.Validators

/** Tạo tài khoản mới, lưu trực tiếp vào bảng users. */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper.getInstance(this)

        binding.btnRegister.setOnClickListener { attemptRegister() }
        binding.textBackToLogin.setOnClickListener { finish() }
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun attemptRegister() {
        val username = binding.editUsername.text?.toString()?.trim().orEmpty()
        val password = binding.editPassword.text?.toString()?.trim().orEmpty()
        val confirmPassword = binding.editConfirmPassword.text?.toString()?.trim().orEmpty()

        if (!Validators.validateUsername(username)) {
            showError(getString(R.string.error_username_empty))
            return
        }
        if (!Validators.validatePassword(password)) {
            showError(getString(R.string.error_password_short))
            return
        }
        if (password != confirmPassword) {
            showError(getString(R.string.error_password_mismatch))
            return
        }
        if (dbHelper.usernameExists(username)) {
            showError(getString(R.string.error_username_exists))
            return
        }

        if (dbHelper.insertUser(username, password)) {
            hideError()
            finish()
        } else {
            showError(getString(R.string.error_username_exists))
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
