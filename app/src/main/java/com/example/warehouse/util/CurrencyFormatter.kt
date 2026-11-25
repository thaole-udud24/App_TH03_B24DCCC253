package com.example.warehouse.util

import java.text.NumberFormat
import java.util.Locale

/** Định dạng số tiền theo chuẩn Việt Nam, ví dụ 150000 -> "150.000 đ". */
object CurrencyFormatter {

    private val numberFormat = NumberFormat.getNumberInstance(Locale("vi", "VN"))

    fun format(amount: Long): String = "${numberFormat.format(amount)} đ"
}
