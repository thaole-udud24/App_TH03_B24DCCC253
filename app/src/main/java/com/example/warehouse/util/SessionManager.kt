package com.example.warehouse.util

import android.content.Context

/**
 * Lưu session đơn giản bằng SharedPreferences, chỉ để hiển thị tên và đăng xuất.
 * App vẫn yêu cầu đăng nhập lại mỗi lần mở (không dùng để bỏ qua màn Login).
 */
object SessionManager {

    private const val PREFS_NAME = "warehouse_session"
    private const val KEY_USERNAME = "username"

    fun saveUsername(context: Context, username: String) {
        prefs(context).edit().putString(KEY_USERNAME, username).apply()
    }

    fun getUsername(context: Context): String? = prefs(context).getString(KEY_USERNAME, null)

    fun clear(context: Context) {
        prefs(context).edit().remove(KEY_USERNAME).apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
