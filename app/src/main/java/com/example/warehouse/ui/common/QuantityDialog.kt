package com.example.warehouse.ui.common

import android.content.Context
import android.widget.EditText
import android.widget.LinearLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.warehouse.R

/** Hộp thoại nhập số lượng tùy ý cho Nhập thêm / Bán. */
object QuantityDialog {

    fun show(context: Context, onConfirm: (Int) -> Unit) {
        val input = EditText(context).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            hint = context.getString(R.string.hint_quantity)
        }
        val padding = (16 * context.resources.displayMetrics.density).toInt()
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(padding, padding, padding, padding)
            addView(input)
        }

        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.quantity_dialog_title)
            .setView(container)
            .setPositiveButton(R.string.btn_confirm) { _, _ ->
                val amount = input.text.toString().toIntOrNull()
                if (amount != null && amount > 0) {
                    onConfirm(amount)
                }
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }
}
