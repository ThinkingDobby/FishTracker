package com.example.thinkingdobby.fishtracker

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) { /* ... */
                        report.let {
                            if (report.areAllPermissionsGranted()) {
                                startLoading()
                            } else {
                                showSettingsDialog()
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                        showSettingsDialog()
                    }
                }).check()

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.need_permission)
        builder.setPositiveButton("설정으로 이동") { dialog, _ ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton("취소") { dialog, _ -> dialog.cancel() }
        builder.show()
    }


    // 어플리케이션 정보 설정 페이지
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    private fun startLoading() {
        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, TrackerActivity::class.java)
            finish()
            startActivity(intent)
        }, 700)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
