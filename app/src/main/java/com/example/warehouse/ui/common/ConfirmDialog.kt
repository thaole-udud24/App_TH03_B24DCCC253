package com.example.warehouse.ui.common

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.warehouse.R

/** Hộp thoại xác nhận dùng chung cho các hành động nguy hiểm (ví dụ xóa sản phẩm). */
object ConfirmDialog {

    fun show(context: Context, title: String, message: String, onConfirm: () -> Unit) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.btn_confirm) { _, _ -> onConfirm() }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }
}
