package com.example.warehouse.ui.detail

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.warehouse.R
import com.example.warehouse.data.DbContract
import com.example.warehouse.databinding.ItemStockLogBinding
import com.example.warehouse.model.StockLog
import com.example.warehouse.util.DateFormatter

/** Hiển thị lịch sử nhập/bán dạng timeline; mint = nhập, pink = bán. */
class StockLogAdapter(private val logs: MutableList<StockLog> = mutableListOf()) :
    RecyclerView.Adapter<StockLogAdapter.LogViewHolder>() {

    fun submitList(newLogs: List<StockLog>) {
        logs.clear()
        logs.addAll(newLogs)
        notifyDataSetChanged()
    }

    fun prependLog(log: StockLog) {
        logs.add(0, log)
        notifyItemInserted(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val binding = ItemStockLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) = holder.bind(logs[position])

    override fun getItemCount() = logs.size

    class LogViewHolder(private val binding: ItemStockLogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(log: StockLog) {
            val context = binding.root.context
            val isIn = log.type == DbContract.StockLogTable.TYPE_IN
            val color = ContextCompat.getColor(context, if (isIn) R.color.mint else R.color.pink)

            binding.dotIndicator.backgroundTintList = ColorStateList.valueOf(color)
            binding.textLogAmount.text = context.getString(
                if (isIn) R.string.stock_log_in_format else R.string.stock_log_out_format, log.amount
            )
            binding.textLogTime.text = DateFormatter.format(log.createdAt)
        }
    }
}
