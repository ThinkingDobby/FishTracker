package com.example.thinkingdobby.fishtracker

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.wonderkiln.camerakit.CameraKit
import com.wonderkiln.camerakit.CameraListener
import com.wonderkiln.camerakit.CameraView
import com.yongchun.library.view.ImageSelectorActivity
import kotlinx.android.synthetic.main.activity_tracker.*
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class TrackerActivity : AppCompatActivity(), View.OnClickListener {
    private var classifier: Classifier? = null
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private var cameraView: CameraView? = null
    private var tracker_tv_result: TextView? = null
    private var tracker_tv_resultSub: TextView? = null
    private var tracker_iv_selected: ImageView? = null
    private var tracker_btn_shot: Button? = null
    private var tracker_btn_album: Button? = null
    private var tracker_tv_album: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)
        cameraView = findViewById<View>(R.id.cameraView) as CameraView
        tracker_tv_result = findViewById<View>(R.id.tracker_tv_result) as TextView
        tracker_tv_resultSub = findViewById<View>(R.id.tracker_tv_resultSub) as TextView
        tracker_iv_selected = findViewById<View>(R.id.tracker_iv_selected) as ImageView
        tracker_btn_album = findViewById<View>(R.id.tracker_btn_album) as Button
        tracker_tv_album = findViewById<View>(R.id.tracker_tv_album) as TextView
        tracker_btn_shot = findViewById<View>(R.id.tracker_btn_shot) as Button

        cameraView!!.start()

        initVisibility()

        // scale, seekBar 이용해 크기 조절 구현 시도할 것

        window.apply {
            decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }

        // btn events delegation
        tracker_btn_album!!.setOnClickListener(this)
        tracker_tv_album!!.setOnClickListener(this)
        tracker_btn_shot!!.setOnClickListener(this)

        // initialize tensorflow async
        initTensorFlowAndLoadModel()
        // permission check & request if needed
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) { /* ... */
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) { /* ... */
                    }
                }).check()
        // cameraview library has its own permission check method
        cameraView!!.setPermissions(CameraKit.Constants.PERMISSIONS_PICTURE)
        // invoke tensorflow inference when picture taken from camera
        cameraView!!.setCameraListener(object : CameraListener() {
            override fun onPictureTaken(picture: ByteArray) {
                super.onPictureTaken(picture)
                val bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.size)
                recognize_bitmap(bitmap)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.execute { classifier!!.close() }
    }

    private fun initTensorFlowAndLoadModel() {
        executor.execute {
            classifier = try {
                TensorFlowImageClassifier.create(
                        assets,
                        MODEL_FILE,
                        LABEL_FILE,
                        INPUT_SIZE,
                        IMAGE_MEAN,
                        IMAGE_STD,
                        INPUT_NAME,
                        OUTPUT_NAME)
            } catch (e: Exception) {
                throw RuntimeException("Error initializing TensorFlow!", e)
            }
        }
    }

    override fun onClick(v: View) { // define which methods to call when buttons in view clicked
        val id = v.id
        when (id) {
            R.id.tracker_btn_album, R.id.tracker_tv_album -> LoadImageFromGallery()
            R.id.tracker_btn_shot -> {
                cameraView!!.captureImage()
            }
            else -> {
            }
        }
    }

    // recognize image from camera roll.
    private fun LoadImageFromGallery() {
        // invoke image picker to get a single image to be inferenced
        ImageSelectorActivity.start(this@TrackerActivity, 1, ImageSelectorActivity.MODE_SINGLE, false, false, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) { // pass the selected image from image picker to tensorflow
// image picker returns image(s) in arrayList
        if (resultCode == Activity.RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE) {
            val images = data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT) as ArrayList<String>
            // image decoded to bitmap, which can be recognized by tensorflow
            val bitmap = BitmapFactory.decodeFile(images[0])
            recognize_bitmap(bitmap)
        }
    }

    // retrieves image from entered url string and call tensorflow

    // recognize bitmap and get results
    private fun recognize_bitmap(bitmap: Bitmap) { // create a bitmap scaled to INPUT_SIZE
        var bitmap = bitmap
        bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false)
        // returned value stores in Classifier.Recognition format
// which provides various methods to parse the result,
// but I'm going to show raw result here.
        val results = classifier!!.recognizeImage(bitmap)
        val finalBitmap = bitmap
        runOnUiThread {
            tracker_iv_selected!!.setImageBitmap(finalBitmap)
            visibilityControl()
            tracker_tv_result!!.text = nameEngToKor(namePicker(results[0].toString()))
            if (results.size > 1) tracker_tv_resultSub!!.text =
                   "또는 ${nameEngToKor(namePicker(results[1].toString()))} 입니다."
            else tracker_tv_resultSub!!.text = ""
        }
    }

    private fun initVisibility() {
        tracker_iv_selectedShadow.visibility = View.INVISIBLE
        tracker_tv_guide.visibility = View.VISIBLE
        tracker_tv_resultEnd.visibility = View.INVISIBLE
    }

    private fun visibilityControl() {
        tracker_iv_selectedShadow.visibility = View.VISIBLE
        tracker_tv_guide.visibility = View.INVISIBLE
        tracker_tv_resultEnd.visibility = View.VISIBLE
    }

    private fun namePicker(input: String): String {
        return input.slice(input.lastIndexOf(']') + 1 until input.indexOf('(')).trim()
    }

    private fun nameEngToKor(input: String): String {
        return when (input) {
            "chub" -> "끄리"
            "sweet" -> "은어"
            "masou" -> "산천어"
            "catfish" -> "메기"
            "carassius" -> "붕어"
            "mandarinfish" -> "잉어"
            "blue" -> "블루길"
            "chineseminnow" -> "버들치"
            "darkchub" -> "갈겨니"
            "carp" -> "잉어"
            "eel" -> "장어"
            "leather" -> "향어"
            "skin" -> "누치"
            "minnow" -> "피라미"
            "goby" -> "모래무지"
            "rainbow" -> "무지개송어"
            "bass" -> "배스"
            "coreoperca" -> "꺽지"
            "striped" -> "돌고기"
            "channaargus" -> "가물치"
            "skygager" -> "강준치"
            else -> "Err"
        }
    }

    companion object {
        // INPUT SIZE, MEAN, STD values are taken from label_image source
        private const val INPUT_SIZE = 299
        private const val IMAGE_MEAN = 0
        private const val IMAGE_STD = 255.0f
        private const val INPUT_NAME = "Mul"
        private const val OUTPUT_NAME = "final_result"
        private const val MODEL_FILE = "file:///android_asset/rounded_graph.pb"
        private const val LABEL_FILE = "file:///android_asset/retrained_labels.txt"
    }
}
