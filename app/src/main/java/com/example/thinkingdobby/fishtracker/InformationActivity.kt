package com.example.thinkingdobby.fishtracker

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_information.*

class InformationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)

        information_v_backArea.setOnClickListener {
            finish()
        }

        window.apply {
            decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.none, R.anim.none)
    }
}
