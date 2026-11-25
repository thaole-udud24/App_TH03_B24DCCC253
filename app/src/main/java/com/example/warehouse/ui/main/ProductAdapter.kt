package com.example.warehouse.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.warehouse.R
import com.example.warehouse.databinding.ItemProductBinding
import com.example.warehouse.model.Product
import com.example.warehouse.util.AppConstants
import com.example.warehouse.util.CurrencyFormatter
import com.example.warehouse.util.fadeSlideIn

/** Hiển thị danh sách sản phẩm; chỉ nhận ArrayList kết quả truy vấn, không tự lưu dữ liệu chính. */
class ProductAdapter(
    private val onItemClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.fadeSlideIn(position * 50L)
    }

    fun productAt(position: Int): Product = getItem(position)

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            val context = binding.root.context
            val categoryColor = ContextCompat.getColor(context, AppConstants.colorResForCategory(product.category))

            binding.imgCategoryIcon.setImageResource(AppConstants.iconResForCategory(product.category))
            binding.imgCategoryIcon.backgroundTintList = android.content.res.ColorStateList.valueOf(categoryColor)

            binding.textName.text = product.name
            binding.textPrice.text = CurrencyFormatter.format(product.price)
            binding.textQuantity.text = product.quantity.toString()

            binding.textCategory.text = product.category

            val isLowStock = product.quantity < AppConstants.LOW_STOCK_THRESHOLD
            binding.textLowStockLabel.visibility = if (isLowStock) android.view.View.VISIBLE else android.view.View.GONE
            binding.textQuantity.setTextColor(
                ContextCompat.getColor(context, if (isLowStock) R.color.warning else R.color.text_primary)
            )

            binding.textInactiveLabel.visibility = if (!product.isActive) android.view.View.VISIBLE else android.view.View.GONE
            binding.root.alpha = if (product.isActive) 1f else 0.55f

            binding.root.setOnClickListener { onItemClick(product) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Product, newItem: Product) =
                oldItem.name == newItem.name &&
                    oldItem.price == newItem.price &&
                    oldItem.quantity == newItem.quantity &&
                    oldItem.category == newItem.category &&
                    oldItem.isActive == newItem.isActive
        }
    }
}
