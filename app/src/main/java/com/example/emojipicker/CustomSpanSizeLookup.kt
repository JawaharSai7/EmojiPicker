package com.example.emojipicker


import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CustomSpanSizeLookup(
    private val items: List<Any>,
    private val spanCount: Int
) : GridLayoutManager.SpanSizeLookup() {

    override fun getSpanSize(position: Int): Int {
        return if (items[position] is String) spanCount else 1
    }
}
