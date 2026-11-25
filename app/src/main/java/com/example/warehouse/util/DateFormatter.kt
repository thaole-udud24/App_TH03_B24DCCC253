package com.example.warehouse.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Định dạng mốc thời gian (millis) thành chuỗi ngày giờ Việt Nam cho timeline nhập/bán. */
object DateFormatter {

    private val formatter = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale("vi", "VN"))

    fun format(timeMillis: Long): String = formatter.format(Date(timeMillis))
}
