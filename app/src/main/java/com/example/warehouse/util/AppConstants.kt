package com.example.warehouse.util

import com.example.warehouse.R

/** Hằng số dùng chung toàn app: ngưỡng nghiệp vụ, khóa Intent, loại sản phẩm, màu theo loại. */
object AppConstants {

    const val LOW_STOCK_THRESHOLD = 5

    const val EXTRA_PRODUCT_ID = "extra_product_id"

    const val CATEGORY_ELECTRONICS = "Điện tử"
    const val CATEGORY_FOOD = "Thực phẩm"
    const val CATEGORY_OFFICE = "Văn phòng phẩm"
    const val CATEGORY_HOUSEHOLD = "Gia dụng"
    const val CATEGORY_OTHER = "Khác"

    val ALL_CATEGORIES = listOf(
        CATEGORY_ELECTRONICS, CATEGORY_FOOD, CATEGORY_OFFICE, CATEGORY_HOUSEHOLD, CATEGORY_OTHER
    )

    const val SORT_NAME_ASC = "name_asc"
    const val SORT_PRICE_ASC = "price_asc"
    const val SORT_PRICE_DESC = "price_desc"
    const val SORT_QUANTITY_ASC = "quantity_asc"
    const val SORT_QUANTITY_DESC = "quantity_desc"

    val SORT_OPTIONS = listOf(
        SORT_NAME_ASC, SORT_PRICE_ASC, SORT_PRICE_DESC, SORT_QUANTITY_ASC, SORT_QUANTITY_DESC
    )

    /** Màu nền pastel đại diện cho từng loại sản phẩm, dùng cho chip/avatar/biểu đồ. */
    fun colorResForCategory(category: String): Int = when (category) {
        CATEGORY_ELECTRONICS -> R.color.sky
        CATEGORY_FOOD -> R.color.mint
        CATEGORY_OFFICE -> R.color.lemon
        CATEGORY_HOUSEHOLD -> R.color.peach
        else -> R.color.lavender
    }

    /** Icon vector đại diện cho từng loại sản phẩm, dùng cho chip lọc và ô icon sản phẩm. */
    fun iconResForCategory(category: String): Int = when (category) {
        CATEGORY_ELECTRONICS -> R.drawable.ic_category_electronics
        CATEGORY_FOOD -> R.drawable.ic_category_food
        CATEGORY_OFFICE -> R.drawable.ic_category_office
        CATEGORY_HOUSEHOLD -> R.drawable.ic_category_household
        else -> R.drawable.ic_category_other
    }
}
