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
            // 기본 데이터 추가
            thread {    // 비동기 처리 필요
                // 테스트
                fishDB = FishDB.getInstance(this@MenuActivity)
                fishDB?.fishDao()?.deleteAll()

                val uri = Uri.parse("android.resource://com.example.thinkingdobby.fishtracker/drawable/collection_icon_carp")
                val ei = ExifInterface(createCopyAndReturnRealPath(applicationContext, uri))
                val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED)

                val bass = Fish()
                bass.fishName = "배스"
                bass.id = 1
                bass.imgOt = orientation
                bass.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(bass)

                val blue = Fish()
                blue.fishName = "블루길"
                blue.id = 2
                blue.imgOt = orientation
                blue.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(blue)

                val carrasius = Fish()
                carrasius.fishName = "붕어"
                carrasius.id = 3
                carrasius.imgOt = orientation
                carrasius.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(carrasius)

                val carp = Fish()
                carp.fishName = "잉어"
                carp.id = 4
                carp.imgOt = orientation
                carp.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(carp)

                val catfish = Fish()
                catfish.fishName = "메기"
                catfish.id = 5
                catfish.imgOt = orientation
                catfish.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(catfish)

                val channaArgus = Fish()
                channaArgus.fishName = "가물치"
                channaArgus.id = 6
                channaArgus.imgOt = orientation
                channaArgus.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(channaArgus)

                val chineseMinnow = Fish()
                chineseMinnow.fishName = "버들치"
                chineseMinnow.id = 7
                chineseMinnow.imgOt = orientation
                chineseMinnow.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(chineseMinnow)

                val chub = Fish()
                chub.fishName = "끄리"
                chub.id = 8
                chub.imgOt = orientation
                chub.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(chub)

                val coreoperca = Fish()
                coreoperca.fishName = "꺽지"
                coreoperca.id = 9
                coreoperca.imgOt = orientation
                coreoperca.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(coreoperca)

                val darkChub = Fish()
                darkChub.fishName = "갈겨니"
                darkChub.id = 10
                darkChub.imgOt = orientation
                darkChub.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(darkChub)

                val eel = Fish()
                eel.fishName = "장어"
                eel.id = 11
                eel.imgOt = orientation
                eel.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(eel)

                val goby = Fish()
                goby.fishName = "모래무지"
                goby.id = 12
                goby.imgOt = orientation
                goby.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(goby)

                val leather = Fish()
                leather.fishName = "향어"
                leather.id = 13
                leather.imgOt = orientation
                leather.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(leather)

                val mandarinFish = Fish()
                mandarinFish.fishName = "쏘가리"
                mandarinFish.id = 14
                mandarinFish.imgOt = orientation
                mandarinFish.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(mandarinFish)

                val masou = Fish()
                masou.fishName = "산천어"
                masou.id = 15
                masou.imgOt = orientation
                masou.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(masou)

                val minnow = Fish()
                minnow.fishName = "피라미"
                minnow.id = 16
                minnow.imgOt = orientation
                minnow.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(minnow)

                val rainbow = Fish()
                rainbow.fishName = "무지개 송어"
                rainbow.id = 17
                rainbow.imgOt = orientation
                rainbow.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(rainbow)

                val skin = Fish()
                skin.fishName = "누치"
                skin.id = 18
                skin.imgOt = orientation
                skin.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(skin)

                val skygager = Fish()
                skygager.fishName = "강준치"
                skygager.id = 19
                skygager.imgOt = orientation
                skygager.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(skygager)

                val striped = Fish()
                striped.fishName = "돌고기"
                striped.id = 20
                striped.imgOt = orientation
                striped.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(striped)

                val sweet = Fish()
                sweet.fishName = "은어"
                sweet.id = 21
                sweet.imgOt = orientation
                sweet.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(sweet)
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)

        return stream.toByteArray()
    }
}
