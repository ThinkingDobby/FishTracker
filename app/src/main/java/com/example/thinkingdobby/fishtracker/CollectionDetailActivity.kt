package com.example.thinkingdobby.fishtracker

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.ExifInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.thinkingdobby.fishtracker.data.Fish
import com.example.thinkingdobby.fishtracker.functions.rotateImage
import kotlinx.android.synthetic.main.activity_collection_detail.*

class CollectionDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_detail)

        window.apply {
            decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }

        collectionDetail_v_backArea.setOnClickListener {
            finish()
        }

        val bundle = intent.extras
        val fish = bundle?.getParcelable<Fish>("selectedFish")!!

        collectionDetail_tv_titleMain.text = fish.fishName
        collectionDetail_tv_date.text = fish.date
        collectionDetail_tv_location.text = fish.location
        collectionDetail_tv_count.text = fish.count.toString()
        collectionDetail_tv_size.text = fish.size.toString()
        collectionDetail_tv_info.text = fish.info

        val options = BitmapFactory.Options()
        val bitmap = BitmapFactory.decodeByteArray(fish.image, 0, fish.image!!.size, options)

        val rotatedBitmap = when (fish.imgOt) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> bitmap
        }

        collectionDetail_cv_iv_fish.setImageBitmap(rotatedBitmap)

        collectionDetail_btn_change.setOnClickListener {
            val intent = Intent(this, CollectionDetailEditActivity::class.java)
            val fishBundle = Bundle()
            fishBundle.putParcelable("selectedFish", fish)
            intent.putExtras(fishBundle)
            startActivity(intent)
            finish()
        }
    }
}
