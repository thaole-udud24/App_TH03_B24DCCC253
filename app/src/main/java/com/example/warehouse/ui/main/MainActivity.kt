package com.example.warehouse.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.example.warehouse.R
import com.example.warehouse.data.DatabaseHelper
import com.example.warehouse.databinding.ActivityMainBinding
import com.example.warehouse.model.Product
import com.example.warehouse.ui.add.AddProductActivity
import com.example.warehouse.ui.detail.ProductDetailActivity
import com.example.warehouse.ui.login.LoginActivity
import com.example.warehouse.util.AppConstants
import com.example.warehouse.util.CurrencyFormatter
import com.example.warehouse.util.SessionManager
import com.example.warehouse.util.animateCount

/** Danh sách sản phẩm: thống kê, tìm kiếm, lọc, sắp xếp, vuốt xóa, pull-to-refresh. */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: ProductAdapter

    private var keyword: String? = null
    private var selectedCategory: String? = null
    private var onlyLowStock = false
    private var onlyInactive = false
    private var sortBy = AppConstants.SORT_NAME_ASC

    private var lastTotalValue = 0L
    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var isChartExpanded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper.getInstance(this)
        setupGreeting()
        setupRecyclerView()
        setupSearch()
        setupFilterChips()
        setupSort()
        setupSwipeRefresh()
        setupFab()
        setupChartToggle()
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun setupGreeting() {
        val username = SessionManager.getUsername(this) ?: "admin"
        binding.textGreeting.text = getString(R.string.greeting_format, username)
        binding.btnLogout.setOnClickListener {
            SessionManager.clear(this)
            startActivity(
                Intent(this, LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter { product ->
            startActivity(
                Intent(this, ProductDetailActivity::class.java)
                    .putExtra(AppConstants.EXTRA_PRODUCT_ID, product.id)
            )
        }
        binding.recyclerProducts.layoutManager = LinearLayoutManager(this)
        binding.recyclerProducts.adapter = adapter

        ItemTouchHelper(SwipeToDeleteCallback { position ->
            if (position != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                deleteProduct(adapter.productAt(position))
            }
        }).attachToRecyclerView(binding.recyclerProducts)
    }

    private fun setupSearch() {
        binding.editSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                val runnable = Runnable {
                    keyword = s?.toString()
                    refreshList()
                }
                searchRunnable = runnable
                searchHandler.postDelayed(runnable, 300L)
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun setupFilterChips() {
        val chipToCategory = mapOf(
            binding.chipElectronics.id to AppConstants.CATEGORY_ELECTRONICS,
            binding.chipFood.id to AppConstants.CATEGORY_FOOD,
            binding.chipOffice.id to AppConstants.CATEGORY_OFFICE,
            binding.chipHousehold.id to AppConstants.CATEGORY_HOUSEHOLD,
            binding.chipOther.id to AppConstants.CATEGORY_OTHER
        )

        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            val checkedId = checkedIds.firstOrNull() ?: binding.chipAll.id
            selectedCategory = chipToCategory[checkedId]
            onlyLowStock = checkedId == binding.chipLowStock.id
            onlyInactive = checkedId == binding.chipInactive.id
            refreshList()
        }
    }

    private fun setupSort() {
        binding.textSort.setOnClickListener { showSortDialog() }
        binding.btnFilter.setOnClickListener { showSortDialog() }
    }

    private fun showSortDialog() {
        val options = resources.getStringArray(R.array.sort_options)
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setItems(options) { _, index ->
                sortBy = AppConstants.SORT_OPTIONS[index]
                binding.textSort.text = options[index]
                refreshList()
            }
            .show()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            refreshList()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }
    }

    private fun setupChartToggle() {
        binding.textChartToggle.setOnClickListener {
            isChartExpanded = !isChartExpanded
            binding.textChartToggle.setText(
                if (isChartExpanded) R.string.action_collapse else R.string.action_view_detail
            )
            updateChart()
        }
    }

    private fun deleteProduct(product: Product) {
        dbHelper.deleteProduct(product.id)
        refreshList()
        Snackbar.make(binding.root, getString(R.string.undo_delete_message, product.name), Snackbar.LENGTH_LONG)
            .setAction(R.string.action_undo) {
                dbHelper.restoreProduct(product)
                refreshList()
            }
            .show()
    }

    private fun refreshList() {
        val products = when {
            onlyInactive -> dbHelper.getInactiveProducts(keyword, sortBy)
            else -> dbHelper.searchProducts(keyword, selectedCategory, onlyLowStock, sortBy)
        }
        adapter.submitList(products)
        updateEmptyState(products.isEmpty())
        updateStats()
        updateChart()
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.layoutEmpty.visibility = if (isEmpty) android.view.View.VISIBLE else android.view.View.GONE
        binding.recyclerProducts.visibility = if (isEmpty) android.view.View.GONE else android.view.View.VISIBLE

        val isFiltering = !keyword.isNullOrBlank() || selectedCategory != null || onlyLowStock || onlyInactive
        if (isFiltering) {
            binding.imgEmpty.setImageResource(R.drawable.ic_no_result)
            binding.textEmptyTitle.setText(R.string.empty_search_title)
            binding.textEmptyMessage.setText(R.string.empty_search_message)
        } else {
            binding.imgEmpty.setImageResource(R.drawable.ic_empty_box)
            binding.textEmptyTitle.setText(R.string.empty_list_title)
            binding.textEmptyMessage.setText(R.string.empty_list_message)
        }
    }

    private fun updateStats() {
        val totalProducts = dbHelper.countAllProducts()
        val totalValue = dbHelper.getTotalValue()
        val lowStockCount = dbHelper.countLowStock()

        binding.textTotalProducts.text = totalProducts.toString()
        binding.textLowStock.text = lowStockCount.toString()
        binding.textTotalValue.animateCount(lastTotalValue, totalValue) { CurrencyFormatter.format(it) }
        lastTotalValue = totalValue
    }

    private fun updateChart() {
        val maxBars = if (isChartExpanded) Int.MAX_VALUE else 2
        binding.categoryBarView.setData(dbHelper.getValueByCategory(), maxBars)
    }
}
