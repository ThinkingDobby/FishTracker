package com.example.thinkingdobby.fishtracker

import android.content.Intent
import android.graphics.Bitmap
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.thinkingdobby.fishtracker.data.Fish
import com.example.thinkingdobby.fishtracker.data.FishDB
import com.example.thinkingdobby.fishtracker.functions.createCopyAndReturnRealPath
import kotlinx.android.synthetic.main.activity_menu.*
import java.io.*
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
            thread {    // 비동기 처리 필요
                // 테스트
                fishDB = FishDB.getInstance(this@MenuActivity)
                fishDB?.fishDao()?.deleteAll()

                val uri = Uri.parse("android.resource://com.example.thinkingdobby.fishtracker/drawable/collection_icon_carp")
                val ei = ExifInterface(createCopyAndReturnRealPath(applicationContext, uri))
                val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED)

                val carrasius = Fish()
                carrasius.date = "아직 수집되지 않았음"
                carrasius.fishName = "붕어"
                carrasius.id = 1
                carrasius.imgOt = orientation
                carrasius.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(carrasius)

                val carp = Fish()
                carp.date = "아직 수집되지 않았음"
                carp.fishName = "잉어"
                carp.id = 2
                carp.imgOt = orientation
                carp.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(carp)
            }
        } else {
            // 테스트 위함 - 최초 실행 환경으로 전환
            val editor = pref.edit()
            editor.putBoolean("isFirst", false)
            editor.apply()
        }
    }

    private fun getByteArrayFromDrawable(uri: Uri): ByteArray {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream)

        return stream.toByteArray()
    }
}
