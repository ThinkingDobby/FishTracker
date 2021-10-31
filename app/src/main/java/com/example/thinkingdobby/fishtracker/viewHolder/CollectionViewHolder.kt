package com.example.thinkingdobby.fishtracker.viewHolder

import android.graphics.Bitmap
import android.graphics.Matrix
import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.thinkingdobby.fishtracker.data.Fish
import kotlinx.android.synthetic.main.fish_card.view.*

class CollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val fish_cv_iv_fish = itemView.fish_cv_iv_fish
    val fish_cv_tv_name = itemView.fish_cv_tv_name
    val fish_cv_tv_date = itemView.fish_cv_tv_date

    fun bind(fish: Fish, bitmap: Bitmap) {
        fish_cv_tv_name.text = fish.fishName
        fish_cv_tv_date.text = fish.date

        fun imgRotate(bmp: Bitmap): Bitmap {
            val width = bmp.width
            val height = bmp.height

            val matrix = Matrix()
            matrix.postRotate((90).toFloat())

            val resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true)
            bmp.recycle()

            return resizedBitmap
        }

        fish_cv_iv_fish.setImageBitmap(imgRotate(bitmap))
    }
}