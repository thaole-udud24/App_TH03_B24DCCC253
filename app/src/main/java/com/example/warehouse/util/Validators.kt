package com.example.warehouse.util

/** Kết quả kiểm tra một trường nhập: null nếu hợp lệ, ngược lại là thông báo lỗi. */
object Validators {

    fun validateProductName(name: String): Boolean = name.trim().isNotEmpty()

    fun validatePrice(priceText: String): Boolean {
        val price = priceText.toLongOrNull() ?: return false
        return price > 0
    }

    fun validateQuantity(quantityText: String): Boolean {
        val quantity = quantityText.toIntOrNull() ?: return false
        return quantity >= 0
    }

    fun validateUsername(username: String): Boolean = username.trim().isNotEmpty()

    fun validatePassword(password: String): Boolean = password.length >= 6
}
