package com.example.warehouse.ui.detail

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.warehouse.R
import com.example.warehouse.data.DatabaseHelper
import com.example.warehouse.databinding.ActivityProductDetailBinding
import com.example.warehouse.model.Product
import com.example.warehouse.ui.add.AddProductActivity
import com.example.warehouse.ui.common.ConfirmDialog
import com.example.warehouse.ui.common.QuantityDialog
import com.example.warehouse.util.AppConstants
import com.example.warehouse.util.CurrencyFormatter
import com.example.warehouse.util.bounceOnClick

/** Chi tiết sản phẩm: nhận id qua Intent, luôn truy vấn lại DB theo id để hiển thị. */
class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var logAdapter: StockLogAdapter

    private var productId: Long = -1L
    private var isProcessing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper.getInstance(this)
        productId = intent.getLongExtra(AppConstants.EXTRA_PRODUCT_ID, -1L)

        logAdapter = StockLogAdapter()
        binding.recyclerStockLogs.layoutManager = LinearLayoutManager(this)
        binding.recyclerStockLogs.adapter = logAdapter

        binding.btnBack.setOnClickListener { finish() }
        binding.btnEdit.setOnClickListener {
            startActivity(
                Intent(this, AddProductActivity::class.java)
                    .putExtra(AppConstants.EXTRA_PRODUCT_ID, productId)
            )
        }
        binding.btnDelete.setOnClickListener { confirmDelete() }

        binding.btnAddOne.setOnClickListener { it.bounceOnClick(); applyStockChange(1, isIn = true) }
        binding.btnSellOne.setOnClickListener { it.bounceOnClick(); applyStockChange(1, isIn = false) }
        binding.btnAddCustom.setOnClickListener { QuantityDialog.show(this) { amount -> applyStockChange(amount, isIn = true) } }
        binding.btnSellCustom.setOnClickListener { QuantityDialog.show(this) { amount -> applyStockChange(amount, isIn = false) } }
    }

    override fun onResume() {
        super.onResume()
        loadProduct()
    }

    private fun loadProduct() {
        val product = dbHelper.getProductById(productId)
        if (product == null) {
            finish()
            return
        }
        bindProduct(product)
        val logs = dbHelper.getStockLogs(productId)
        logAdapter.submitList(logs)
        binding.recyclerStockLogs.visibility = if (logs.isEmpty()) android.view.View.GONE else android.view.View.VISIBLE
        binding.layoutLogEmpty.visibility = if (logs.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
    }

    private fun bindProduct(product: Product) {
        binding.textName.text = product.name
        val categoryColor = androidx.core.content.ContextCompat.getColor(this, AppConstants.colorResForCategory(product.category))
        binding.chipCategory.text = product.category
        binding.chipCategory.chipBackgroundColor = android.content.res.ColorStateList.valueOf(categoryColor)
        binding.imgCategoryIcon.setImageResource(AppConstants.iconResForCategory(product.category))
        binding.imgCategoryIcon.backgroundTintList = android.content.res.ColorStateList.valueOf(categoryColor)
        binding.textPrice.text = CurrencyFormatter.format(product.price)
        binding.textQuantity.text = product.quantity.toString()
        binding.textStockValue.text = CurrencyFormatter.format(product.getStockValue())
        binding.textDescription.text = product.description.takeUnless { it.isNullOrBlank() } ?: "—"

        binding.textLowStockWarning.visibility =
            if (product.quantity < AppConstants.LOW_STOCK_THRESHOLD) android.view.View.VISIBLE else android.view.View.GONE

        binding.switchActive.setOnCheckedChangeListener(null)
        binding.switchActive.isChecked = product.isActive
        binding.switchActive.setOnCheckedChangeListener { _, isChecked ->
            dbHelper.updateActive(productId, isChecked)
            updateActiveUi(isChecked)
        }
        updateActiveUi(product.isActive)
    }

    private fun updateActiveUi(active: Boolean) {
        binding.textInactiveLabel.visibility = if (active) android.view.View.GONE else android.view.View.VISIBLE
        binding.btnSellOne.isEnabled = active
        binding.btnSellCustom.isEnabled = active
        binding.btnSellCustom.alpha = if (active) 1f else 0.4f
    }

    private fun applyStockChange(amount: Int, isIn: Boolean) {
        if (isProcessing) return
        val product = dbHelper.getProductById(productId) ?: return

        if (!isIn && product.quantity == 0) {
            Toast.makeText(this, R.string.out_of_stock_message, Toast.LENGTH_SHORT).show()
            return
        }
        if (!isIn && amount > product.quantity) {
            Toast.makeText(this, R.string.error_sell_exceeds_stock, Toast.LENGTH_SHORT).show()
            return
        }

        isProcessing = true
        dbHelper.applyStockChange(productId, amount, isIn)
        isProcessing = false
        loadProduct()
    }

    private fun confirmDelete() {
        val product = dbHelper.getProductById(productId) ?: return
        ConfirmDialog.show(
            this,
            getString(R.string.delete_confirm_title),
            getString(R.string.delete_confirm_message_format, product.name)
        ) {
            dbHelper.deleteProduct(productId)
            finish()
        }
    }
}
