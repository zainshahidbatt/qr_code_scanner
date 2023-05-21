package com.example.qrcodescanner.ui.fragment


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.qrcodescanner.R
import com.example.qrcodescanner.database.LocaleDataBase
import com.example.qrcodescanner.databinding.FragmentQrscannerBinding
import com.example.qrcodescanner.roomdb.utils.DbHelper
import com.example.qrcodescanner.roomdb.utils.DbHelperI
import com.example.qrcodescanner.ui.base.BaseFragment
import com.example.qrcodescanner.ui.dialog.ResultBottomSheetDialog
import com.example.qrcodescanner.ui.dialog.ResultBottomSheetDialog.Companion.showResultDialog
import dagger.hilt.android.AndroidEntryPoint
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView


@Suppress("SameParameterValue")
@AndroidEntryPoint
class QRScannerFragment : BaseFragment<FragmentQrscannerBinding>(), ZBarScannerView.ResultHandler {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentQrscannerBinding =
        FragmentQrscannerBinding::inflate

  val viewModel by viewModels<ScannedViewModel>()

    override fun onCreatedView() {
        init()
        initViews()
        onClicks()
    }

    private lateinit var dbHelperI: DbHelperI
    private lateinit var scannerView: ZBarScannerView

    private lateinit var resultBottomSheetDialog: ResultBottomSheetDialog

    companion object {
        fun newInstance(): QRScannerFragment {
            return QRScannerFragment()
        }
    }


    private fun init() {
        dbHelperI = DbHelper(LocaleDataBase.getAppDatabase(requireContext())!!)
    }

    private fun initViews() {
        initializeQRCamera()
    }

    private fun initializeQRCamera() {
        scannerView = ZBarScannerView(context)
        scannerView.setResultHandler(this)
        scannerView.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorTranslucent
            )
        )
        scannerView.setBorderColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimaryDark
            )
        )
        scannerView.setLaserColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimaryDark
            )
        )
        scannerView.setBorderStrokeWidth(10)
        scannerView.setSquareViewFinder(true)
        scannerView.setupScanner()
        scannerView.setAutoFocus(true)
        startQRCamera()
        binding.containerScanner.addView(scannerView)
    }


    private fun onClicks() {
        binding.flashToggle.setOnClickListener {
            if (binding.flashToggle.isSelected) {
                offFlashLight()
            } else {
                onFlashLight()
            }
        }
    }

    private fun onFlashLight() {
        binding.flashToggle.isSelected = true
        scannerView.flash = true
    }

    private fun offFlashLight() {
        binding.flashToggle.isSelected = false
        scannerView.flash = false
    }

    private fun startQRCamera() {
        scannerView.startCamera()
    }

    private fun onQrResult(contents: String?) {
        if (contents.isNullOrEmpty())
            showToast("Empty Qr Result")
        else
            saveToDataBase(contents)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun setResultDialog() {
        resultBottomSheetDialog.showResultDialog(
            childFragmentManager,
        )


    }

    private fun saveToDataBase(contents: String) {
        scannerView.stopCamera()
        val insertedResultId = dbHelperI.insertQRResult(contents)
        val qrResult = dbHelperI.getQRResult(insertedResultId)
        resultBottomSheetDialog = ResultBottomSheetDialog(qrResult, requireContext()) {
            resetPreview()
        }
        setResultDialog()

//        resultDialog.show(qrResult)


    }

    private fun resetPreview() {
        scannerView.stopCamera()
        scannerView.startCamera()
        scannerView.stopCameraPreview()
        scannerView.resumeCameraPreview(this)
    }


    override fun onResume() {
        super.onResume()
        scannerView.setResultHandler(this)
        scannerView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        scannerView.stopCamera()
    }

    override fun handleResult(result: Result) {
        onQrResult(result.contents)
        scannerView.resumeCameraPreview(this)
    }


}
