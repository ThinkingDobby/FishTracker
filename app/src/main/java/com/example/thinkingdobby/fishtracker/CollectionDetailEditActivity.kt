package com.example.thinkingdobby.fishtracker

import android.Manifest
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.ExifInterface
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.View
import com.example.thinkingdobby.fishtracker.data.Fish
import com.example.thinkingdobby.fishtracker.data.FishDB
import com.example.thinkingdobby.fishtracker.functions.rotateImage
import kotlinx.android.synthetic.main.activity_collection_detail_edit.*
import kotlin.concurrent.thread

class CollectionDetailEditActivity : AppCompatActivity() {
    private var pickImageFromAlbum = 0
    private var uriPhoto: Uri? = null

    private var fishDB: FishDB? = null
    private lateinit var tempImage: ByteArray
    private var tempOt = 0
    private var imageChanged = false
    private var fishId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_detail_edit)

        // request permission
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        // request permission

        window.apply {
            decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }

        collectionDetailEdit_v_backArea.setOnClickListener {
            finish()
        }

        val bundle = intent.extras
        val fish = bundle?.getParcelable<Fish>("selectedFish")!!

        collectionDetailEdit_tv_titleMain.text = fish.fishName
        collectionDetailEdit_tv_date.setText(fish.date)
        collectionDetailEdit_tv_location.setText(fish.location)
        collectionDetailEdit_tv_count.setText(fish.count.toString())
        collectionDetailEdit_tv_size.setText(fish.size.toString())
        collectionDetailEdit_tv_info.setText(fish.info)

        val options = BitmapFactory.Options()
        val bitmap = BitmapFactory.decodeByteArray(fish.image, 0, fish.image!!.size, options)

        val rotatedBitmap = when (fish.imgOt) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> bitmap
        }

        collectionDetailEdit_cv_iv_fish.setImageBitmap(rotatedBitmap)

        collectionDetailEdit_btn_change.setOnClickListener {
            thread {
                fishDB = FishDB.getInstance(this@CollectionDetailEditActivity)

                val updateFish = Fish()
                updateFish.date = collectionDetailEdit_tv_date.text.toString()
                updateFish.location = collectionDetailEdit_tv_location.text.toString()
                updateFish.count = collectionDetailEdit_tv_count.text.toString().toInt()
                updateFish.size = collectionDetailEdit_tv_size.text.toString().toInt()
                updateFish.info = collectionDetailEdit_tv_info.text.toString()

                // 이어서 작성
            }.start()
        }
    }
}
