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

        menu_cv_1_cl.setOnClickListener {
            val intent = Intent(this, CollectionActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.none, R.anim.none)
        }

        menu_cv_4_cl.setOnClickListener {
            val intent = Intent(this, InformationActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.none, R.anim.none)
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
            // 기본 데이터 추가
            thread {    // 비동기 처리 필요
                // 테스트
                fishDB = FishDB.getInstance(this@MenuActivity)
                fishDB?.fishDao()?.deleteAll()

                val uri = Uri.parse("android.resource://com.example.thinkingdobby.fishtracker/drawable/collection_icon_carp")
                val ei = ExifInterface(createCopyAndReturnRealPath(applicationContext, uri))
                val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED)

                // bass
                val uriBass = Uri.parse("android.resource://com.example.thinkingdobby.fishtracker/drawable/collection_icon_bass")
                val eiBass = ExifInterface(createCopyAndReturnRealPath(applicationContext, uriBass))
                val orientationBass = eiBass.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED)

                val bass = Fish()
                bass.fishName = "배스"
                bass.id = 1
                bass.imgOt = orientationBass
                bass.image = getByteArrayFromDrawable(uriBass)
                fishDB?.fishDao()?.insert(bass)

                val blue = Fish()
                blue.fishName = "블루길"
                blue.id = 2
                blue.imgOt = orientation
                blue.image = getByteArrayFromDrawable(uri)
                fishDB?.fishDao()?.insert(blue)

                // carassius
                val uriCara = Uri.parse("android.resource://com.example.thinkingdobby.fishtracker/drawable/collection_icon_carassius")
                val eiCara = ExifInterface(createCopyAndReturnRealPath(applicationContext, uriCara))
                val orientationCara = eiCara.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED)

                val carassius = Fish()
                carassius.fishName = "붕어"
                carassius.id = 3
                carassius.imgOt = orientationCara
                carassius.image = getByteArrayFromDrawable(uriCara)
                fishDB?.fishDao()?.insert(carassius)

                //carp
                val uriCarp = Uri.parse("android.resource://com.example.thinkingdobby.fishtracker/drawable/collection_icon_carp")
                val eiCarp = ExifInterface(createCopyAndReturnRealPath(applicationContext, uriCarp))
                val orientationCarp = eiCarp.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED)

                val carp = Fish()
                carp.fishName = "잉어"
                carp.id = 4
                carp.imgOt = orientationCarp
                carp.image = getByteArrayFromDrawable(uriCarp)
                fishDB?.fishDao()?.insert(carp)

                //catfish
                val uriCat = Uri.parse("android.resource://com.example.thinkingdobby.fishtracker/drawable/collection_icon_catfish")
                val eiCat = ExifInterface(createCopyAndReturnRealPath(applicationContext, uriCat))
                val orientationCat = eiCat.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED)

                val catfish = Fish()
                catfish.fishName = "메기"
                catfish.id = 5
                catfish.imgOt = orientationCat
                catfish.image = getByteArrayFromDrawable(uriCat)
                fishDB?.fishDao()?.insert(catfish)

                //channaargus
                val uriChanna = Uri.parse("android.resource://com.example.thinkingdobby.fishtracker/drawable/collection_icon_channaargus")
                val eiChanna = ExifInterface(createCopyAndReturnRealPath(applicationContext, uriChanna))
                val orientationChanna = eiChanna.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED)

                val channaArgus = Fish()
                channaArgus.fishName = "가물치"
                channaArgus.id = 6
                channaArgus.imgOt = orientationChanna
                channaArgus.image = getByteArrayFromDrawable(uriChanna)
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

                //coreoperca
                val uriCoreo = Uri.parse("android.resource://com.example.thinkingdobby.fishtracker/drawable/collection_icon_coreoperca")
                val eiCoreo = ExifInterface(createCopyAndReturnRealPath(applicationContext, uriCoreo))
                val orientationCoreo = eiCoreo.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED)

                val coreoperca = Fish()
                coreoperca.fishName = "꺽지"
                coreoperca.id = 9
                coreoperca.imgOt = orientationCoreo
                coreoperca.image = getByteArrayFromDrawable(uriCoreo)
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
//            val editor = pref.edit()
//            editor.putBoolean("isFirst", false)
//            editor.apply()
        }
    }

    private fun getByteArrayFromDrawable(uri: Uri): ByteArray {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)

        return stream.toByteArray()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.left_in, R.anim.right_out)
    }
}
