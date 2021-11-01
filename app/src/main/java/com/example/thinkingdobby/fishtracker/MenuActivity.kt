package com.example.thinkingdobby.fishtracker

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.example.thinkingdobby.fishtracker.data.Fish
import com.example.thinkingdobby.fishtracker.data.FishDB
import kotlinx.android.synthetic.main.activity_menu.*
import java.io.ByteArrayOutputStream
import kotlin.concurrent.thread


class MenuActivity : AppCompatActivity() {
    private var fishDB: FishDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        menu_cv_1.setOnClickListener {
            val intent = Intent(this, CollectionActivity::class.java)
            startActivity(intent)
        }

        menu_btn_back.setOnClickListener {
            finish()
        }

        val pref = getSharedPreferences("isFirst", MODE_PRIVATE)
        val first = pref.getBoolean("isFirst", false)
        if (!first) {
            val editor = pref.edit()
            editor.putBoolean("isFirst", true)
            editor.apply()
            Toast.makeText(this@MenuActivity, "Test", Toast.LENGTH_SHORT).show()
            thread {
                fishDB = FishDB.getInstance(this@MenuActivity)
                val fish = Fish()
                fish.date = "11월 1일"
                fish.fishName = "꺽지"
                fish.fishNo = 3
                fish.id = 123
                fish.image = getByteArrayFromDrawable(Uri.parse("android.resource://com.example.thinkingdobby.fishtracker/drawable/collection_icon_carp"))
                fishDB?.fishDao()?.insert(fish)
            }
        } else {
            /*
            val editor = pref.edit()
            editor.putBoolean("isFirst", false)
            editor.apply()

             */
        }
    }

    private fun getByteArrayFromDrawable(uri: Uri): ByteArray {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream)

        return stream.toByteArray()
    }
}
