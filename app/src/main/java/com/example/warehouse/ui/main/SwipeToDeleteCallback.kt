package com.example.warehouse.ui.main

import android.graphics.Canvas
import android.graphics.Paint
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat
import com.example.warehouse.R

/** Vuốt trái một dòng sản phẩm để xóa; chỉ phát hiện thao tác, việc xóa thật nằm ở MainActivity. */
class SwipeToDeleteCallback(
    private val onSwiped: (position: Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    override fun onMove(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
    ) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onSwiped(viewHolder.bindingAdapterPosition)
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        if (dX < 0) {
            val paint = Paint().apply { color = ContextCompat.getColor(recyclerView.context, R.color.warning) }
            c.drawRect(
                itemView.right + dX, itemView.top.toFloat(),
                itemView.right.toFloat(), itemView.bottom.toFloat(), paint
            )
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
