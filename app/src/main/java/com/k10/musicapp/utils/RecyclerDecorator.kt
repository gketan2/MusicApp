package com.k10.musicapp.utils

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView

class RecyclerDecorator(private val offset: Int): RecyclerView.ItemDecoration(){
    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        outRect.top = offset
        outRect.bottom = offset
        outRect.left = offset
        outRect.right = offset
    }
}