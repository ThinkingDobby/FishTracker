package com.example.thinkingdobby.fishtracker.viewHolder

import android.graphics.Bitmap
import android.graphics.Color
import android.media.ExifInterface
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.thinkingdobby.fishtracker.R
import com.example.thinkingdobby.fishtracker.data.Fish
import com.example.thinkingdobby.fishtracker.functions.rotateImage
import kotlinx.android.synthetic.main.fish_card.view.*

class CollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val fish_cv_iv_fish = itemView.fish_cv_iv_fish
    val fish_cv_tv_name = itemView.fish_cv_tv_name
    val fish_cv_tv_date = itemView.fish_cv_tv_date

    fun bind(fish: Fish, bitmap: Bitmap) {
        fish_cv_tv_name.text = fish.fishName
        fish_cv_tv_date.text = fish.date

        if (fish.count.toString().toInt() > 0) {
            fish_cv_tv_name.setTextColor(Color.parseColor("#191919"))
        } else {
            fish_cv_tv_name.setTextColor(Color.parseColor("#676767"))
        }

        val rotatedBitmap = when (fish.imgOt) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> bitmap
        }

        fish_cv_iv_fish.setImageBitmap(rotatedBitmap)
    }
}