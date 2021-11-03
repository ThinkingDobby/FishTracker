package com.example.thinkingdobby.fishtracker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.wonderkiln.camerakit.*
import kotlinx.android.synthetic.main.activity_tracker.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class TrackerActivity : AppCompatActivity(), View.OnClickListener {
    private var classifier: Classifier? = null
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private var cameraView: CameraView? = null
    private val PICK_IMAGE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)

        cameraView = findViewById<View>(R.id.cameraView) as CameraView
        cameraView!!.start()

        initVisibility()
        tracker_tv_resultSubEnd.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        // 상태바 투명화
        window.apply {
            decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }

        tracker_btn_album.setOnClickListener(this)
        tracker_tv_album.setOnClickListener(this)
        tracker_btn_shot.setOnClickListener(this)
        tracker_iv_menu.setOnClickListener {
            val intent = Intent(this@TrackerActivity, MenuActivity::class.java)
            startActivity(intent)
        }

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

        val cameraListener: CameraKitEventListener = object : CameraKitEventListener {
            override fun onEvent(cameraKitEvent: CameraKitEvent) {
            }

            override fun onError(cameraKitError: CameraKitError) {}
            override fun onImage(cameraKitImage: CameraKitImage) {
                val bitmap = BitmapFactory.decodeByteArray(cameraKitImage.jpeg, 0, cameraKitImage.jpeg.size)
                recognize_bitmap(bitmap)
            }

            override fun onVideo(cameraKitVideo: CameraKitVideo) {}
        }

        cameraView!!.addCameraKitListener(cameraListener)
    }




    override fun onDestroy() {
        super.onDestroy()
        executor.execute { classifier!!.close() }
    }

    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.tracker_btn_album, R.id.tracker_tv_album -> loadImageFromGallery()
            R.id.tracker_btn_shot -> cameraView!!.captureImage()
            else -> {
            }
        }
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

    private fun loadImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(Intent.createChooser(intent, "Load Picture"), PICK_IMAGE)
    }

    // 이미지 회전
    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height,
                matrix, true)
    }

    // 이미지 절대 경로 확인
    private fun createCopyAndReturnRealPath(uri: Uri) :String? {
        val context = applicationContext
        val contentResolver = context.contentResolver ?: return null

        // Create file path inside app's data dir
        val filePath = (context.applicationInfo.dataDir + File.separator
                + System.currentTimeMillis())
        val file = File(filePath)
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val outputStream: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                val uriPhoto = data!!.data
                Log.d("uriPhoto", uriPhoto.toString())
                Log.d("realUriPhoto", createCopyAndReturnRealPath(uriPhoto!!))
                val bitmap = MediaStore.Images.Media.getBitmap(applicationContext.contentResolver, uriPhoto)

                // 이미지 회전되는 문제 해결
                if (bitmap != null) {
                    // 이미지 절대 경로 이용해 exif 데이터 추출
                    val ei = ExifInterface(createCopyAndReturnRealPath(uriPhoto!!))
                    val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED)

                    val rotatedBitmap = when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
                        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
                        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
                        ExifInterface.ORIENTATION_NORMAL -> bitmap
                        else -> bitmap
                    }
                    recognize_bitmap(rotatedBitmap)
                }
            }
        }
    }

    private fun initVisibility() {
        tracker_iv_selectedShadow.visibility = View.INVISIBLE
        tracker_tv_guide.visibility = View.VISIBLE
        tracker_tv_resultEnd.visibility = View.INVISIBLE
        tracker_tv_resultSubEnd.visibility = View.INVISIBLE
    }

    private fun setVisibility() {
        tracker_iv_selectedShadow.visibility = View.VISIBLE
        tracker_tv_guide.visibility = View.INVISIBLE
        tracker_tv_resultEnd.visibility = View.VISIBLE
    }

    // recognize bitmap and get results
    private fun recognize_bitmap(bitmap: Bitmap) { // create a bitmap scaled to INPUT_SIZE
        var bitmap = bitmap
        var bitmapForRecognize = bitmap
        if (bitmap.width < INPUT_SIZE || bitmap.height < INPUT_SIZE) {
            bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE * 2, INPUT_SIZE * 2, false)
        }
        bitmapForRecognize = Bitmap.createBitmap(bitmap, (bitmap.width - INPUT_SIZE) / 2,
                (bitmap.height - INPUT_SIZE) / 2,
                INPUT_SIZE,
                INPUT_SIZE
        )
        bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE * 3, INPUT_SIZE * 4, false)

        val results = classifier!!.recognizeImage(bitmapForRecognize)
        val finalBitmap = bitmap
        runOnUiThread {
            tracker_iv_selected.setImageBitmap(finalBitmap)
            setVisibility()
            tracker_tv_resultProbability.text = "${probPicker(results[0].toString())}% 확률로"
            tracker_tv_result.text = nameEngToKor(namePicker(results[0].toString()))
            if (results.size > 1) {
                tracker_tv_resultSub.text =
                        "또는 ${probPicker(results[1].toString())}% 확률로 ${nameEngToKor(namePicker(results[1].toString()))} 입니다."
                if (results.size > 2) tracker_tv_resultSubEnd.visibility = View.VISIBLE
                else tracker_tv_resultSubEnd.visibility = View.INVISIBLE
            } else {
                tracker_tv_resultSub.text = ""
                tracker_tv_resultSubEnd.visibility = View.INVISIBLE
            }
        }
    }

    private fun probPicker(input: String): String {
        return input.slice(input.indexOf('(') + 1 until input.indexOf('.'))
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
