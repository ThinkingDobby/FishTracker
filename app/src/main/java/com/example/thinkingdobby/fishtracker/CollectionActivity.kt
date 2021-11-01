package com.example.thinkingdobby.fishtracker

import android.arch.lifecycle.Observer
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.example.thinkingdobby.fishtracker.adapter.CollectionAdapter
import com.example.thinkingdobby.fishtracker.data.FishDB
import kotlinx.android.synthetic.main.activity_collection.*

class CollectionActivity : AppCompatActivity() {
    private var fishDB: FishDB? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)

        fishDB = FishDB.getInstance(this)

        window.apply {
            decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }

        collection_btn_back.setOnClickListener {
            finish()
        }

        var fishAdapter: CollectionAdapter?
        collection_rv.layoutManager = GridLayoutManager(this@CollectionActivity, 2)
        collection_rv.setHasFixedSize(true)

        fishDB?.fishDao()?.getAll()!!.observe(this, Observer {
            val bitmapList = mutableListOf<Bitmap>()
            for (i in it!!) {
                val options = BitmapFactory.Options()
                options.inSampleSize = 16
                val bitmap = BitmapFactory.decodeByteArray(i.image, 0, i.image!!.size, options)
                bitmapList.add(bitmap)
            }
            fishAdapter = CollectionAdapter(this@CollectionActivity, it, bitmapList)
            collection_rv.adapter = fishAdapter
        })
    }
}
