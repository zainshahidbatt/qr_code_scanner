package com.example.qrcodescanner.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.qrcodescanner.R
import com.example.qrcodescanner.databinding.DialogRemoveAllBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@Suppress("DEPRECATION")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE: Int = 100
    }

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            checkForPermission()
        }, 2000)

    }

    private fun checkForPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
            gotoMain()
        else
            requestForPermission()
    }

    private fun requestForPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.CAMERA),
            REQUEST_CODE
        )
    }

    private fun gotoMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                gotoMain()
            else if (checkForPermanentlyDenied())
                showAppSettingsDialog()
            else
                requestForPermission()

        }

    }

    private fun showAppSettingsDialog() {
        BottomSheetDialog(context, R.style.SheetDialog).also { dialog ->
            val binding = DialogRemoveAllBinding.inflate(layoutInflater)
            dialog.setContentView(binding.root)
            dialog.behavior.isDraggable = false
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.apply {
                binding.title.text = context.getString(R.string.grant_permission)
                binding.subTitle.text = context.getString(R.string.grant_permission_subtitle)
                binding.btnClear.setText(R.string.grant)
                cancelDialog.setOnClickListener {
                    run {
                        finish()
                    }

                }
                btnClear.setOnClickListener {
                    gotoAppSetting()
                    dialog.dismiss()
                }
            }
            dialog.show()
        }
    }

    private fun gotoAppSetting() {
        val intent =
            Intent(ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun checkForPermanentlyDenied(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA).not()
        } else {
            false
        }
    }


    override fun onRestart() {
        super.onRestart()
        checkForPermission()
    }

}
