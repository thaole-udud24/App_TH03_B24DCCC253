package com.example.warehouse.ui.add

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.warehouse.R
import com.example.warehouse.data.DatabaseHelper
import com.example.warehouse.databinding.ActivityAddProductBinding
import com.example.warehouse.model.Product
import com.example.warehouse.util.AppConstants
import com.example.warehouse.util.Validators

/** Thêm sản phẩm mới hoặc sửa sản phẩm đã có (nhận id qua Intent để chuyển sang chế độ Sửa). */
class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private lateinit var dbHelper: DatabaseHelper

    private var editingProduct: Product? = null
    private var isSaving = false
    private var selectedCategoryIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper.getInstance(this)
        binding.btnBack.setOnClickListener { finish() }

        val productId = intent.getLongExtra(AppConstants.EXTRA_PRODUCT_ID, -1L)
        if (productId != -1L) {
            editingProduct = dbHelper.getProductById(productId)
        }

        binding.textCategoryValue.text = AppConstants.ALL_CATEGORIES[selectedCategoryIndex]
        binding.fieldCategory.setOnClickListener { showCategoryPicker() }

        if (editingProduct != null) {
            fillFormForEdit(editingProduct!!)
        }

        binding.btnSave.setOnClickListener { attemptSave() }
    }

    private fun showCategoryPicker() {
        val options = AppConstants.ALL_CATEGORIES.toTypedArray()
        AlertDialog.Builder(this)
            .setItems(options) { _, index ->
                selectedCategoryIndex = index
                binding.textCategoryValue.text = options[index]
            }
            .show()
    }

    private fun fillFormForEdit(product: Product) {
        binding.textTitle.setText(R.string.edit_product_title)
        binding.btnSave.setText(R.string.btn_update_product)
        binding.editName.setText(product.name)
        binding.editPrice.setText(product.price.toString())
        binding.editQuantity.setText(product.quantity.toString())
        binding.switchActive.isChecked = product.isActive
        binding.editDescription.setText(product.description)
        selectedCategoryIndex = AppConstants.ALL_CATEGORIES.indexOf(product.category).coerceAtLeast(0)
        binding.textCategoryValue.text = AppConstants.ALL_CATEGORIES[selectedCategoryIndex]
    }

    private fun attemptSave() {
        if (isSaving) return

        val name = binding.editName.text?.toString()?.trim().orEmpty()
        val priceText = binding.editPrice.text?.toString()?.trim().orEmpty()
        val quantityText = binding.editQuantity.text?.toString()?.trim().orEmpty()

        if (!Validators.validateProductName(name)) {
            binding.editName.error = getString(R.string.error_name_empty)
            return
        }
        if (!Validators.validatePrice(priceText)) {
            binding.editPrice.error = getString(R.string.error_price_invalid)
            return
        }
        if (!Validators.validateQuantity(quantityText)) {
            binding.editQuantity.error = getString(R.string.error_quantity_invalid)
            return
        }

        val isDuplicateName = editingProduct?.name != name && dbHelper.productNameExists(name)
        if (isDuplicateName) {
            Toast.makeText(this, R.string.warning_duplicate_name, Toast.LENGTH_SHORT).show()
        }

        isSaving = true
        binding.btnSave.isEnabled = false

        val category = AppConstants.ALL_CATEGORIES[selectedCategoryIndex]
        val product = Product(
            editingProduct?.id ?: 0L,
            name,
            priceText.toLong(),
            quantityText.toInt(),
            category,
            binding.switchActive.isChecked,
            binding.editDescription.text?.toString()?.trim()
        )

        if (editingProduct != null) {
            dbHelper.updateProduct(product)
        } else {
            dbHelper.insertProduct(product)
        }
        finish()
    }
}
