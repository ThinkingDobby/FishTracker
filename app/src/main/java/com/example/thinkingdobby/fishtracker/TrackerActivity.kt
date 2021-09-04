package com.example.thinkingdobby.fishtracker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
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
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

//import com.flurgle.camerakit.CameraKit;
//import com.flurgle.camerakit.CameraListener;
//import com.flurgle.camerakit.CameraView;
class TrackerActivity : AppCompatActivity(), View.OnClickListener {
    private var classifier: Classifier? = null
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private var cameraView: CameraView? = null
    private var tracker_result_text: TextView? = null
    private var imgResult: ImageView? = null
    private var btnDetect: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)
        cameraView = findViewById<View>(R.id.cameraView) as CameraView
        tracker_result_text = findViewById<View>(R.id.tracker_result_text) as TextView
        imgResult = findViewById<View>(R.id.imgResult) as ImageView
        val btnGallery = findViewById<View>(R.id.btnGallery) as Button
        val btnURL = findViewById<View>(R.id.btnUrl) as Button
        val btnCamera = findViewById<View>(R.id.btnCamera) as Button
        btnDetect = findViewById<View>(R.id.btnDetect) as Button
        cameraView!!.visibility = View.INVISIBLE // hides cameraview on startup, shows when camera button clicked
        btnDetect!!.visibility = View.INVISIBLE // hide camera detect button on startup
        // btn events delegation
        btnGallery.setOnClickListener(this)
        btnURL.setOnClickListener(this)
        btnCamera.setOnClickListener(this)
        btnDetect!!.setOnClickListener(this)
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
            R.id.btnGallery -> LoadImageFromGallery()
            R.id.btnUrl -> LoadImageFromUrl()
            R.id.btnCamera -> DetectImageFromCamera()
            R.id.btnDetect -> cameraView!!.captureImage()
            else -> {
            }
        }
    }

    // recognize image from camera roll.
    private fun LoadImageFromGallery() { // make sure cameraview/dectect button invisible and stopped
        cameraView!!.visibility = View.INVISIBLE
        btnDetect!!.visibility = View.INVISIBLE
        cameraView!!.stop()
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
    private fun LoadImageFromUrl() { // make sure cameraview/dectect button invisible and stopped
        cameraView!!.visibility = View.INVISIBLE
        btnDetect!!.visibility = View.INVISIBLE
        cameraView!!.stop()
        // inflate alertdialog for url input and show it to the user
        val layoutinflater = LayoutInflater.from(this)
        val dialogView = layoutinflater.inflate(R.layout.dialog_prompt_url, null)
        val editURL = dialogView.findViewById<View>(R.id.editURL) as EditText
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(dialogView)
                .setTitle("Enter/Paste url of image to recognize")
                .setPositiveButton("Ok") { dialog, id ->
                    // read from url stream not allowed in main thread. so invoke it in different thread
                    executor.execute {
                        try {
                            val url = editURL.text.toString()
                            val input = URL(editURL.text.toString()).openStream()
                            //InputStream input = new java.net.URL(editURL.getText().toString()).openConnection().getInputStream();
                            val bitmap = BitmapFactory.decodeStream(input)
                            // recognize_bitmap needs to update the UI(imgResult, tracker_result_text), so invoke it in runOnUiThread
                            runOnUiThread {
                                //
                                recognize_bitmap(bitmap)
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
                .setNegativeButton("CANCEL") { dialog, id -> }
                .create()
                .show()
    }

    private fun DetectImageFromCamera() { // show cameraview and detect button when source from camera button clicked
        cameraView!!.visibility = View.VISIBLE
        btnDetect!!.visibility = View.VISIBLE
        if (!cameraView!!.isActivated) cameraView!!.start()
    }

    // recognize bitmap and get results
    private fun recognize_bitmap(bitmap: Bitmap) { // create a bitmap scaled to INPUT_SIZE
        var bitmap = bitmap
        bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false)
        // returned value stores in Classifier.Recognition format
// which provides various methods to parse the result,
// but I'm going to show raw result here.
        val results = classifier!!.recognizeImage(bitmap)
        val finalBitmap = bitmap
        runOnUiThread { imgResult!!.setImageBitmap(finalBitmap) }
        tracker_result_text!!.text = results.toString()
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
