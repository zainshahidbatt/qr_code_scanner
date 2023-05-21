package com.example.qrcodescanner.ui.dialog

import android.content.ClipData
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.ClipboardManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.qrcodescanner.R
import com.example.qrcodescanner.databinding.LayoutQrResultShowBinding
import com.example.qrcodescanner.database.model.QrResults
import com.example.qrcodescanner.roomdb.utils.DbHelper
import com.example.qrcodescanner.roomdb.utils.DbHelperI
import com.example.qrcodescanner.database.LocaleDataBase
import com.example.qrcodescanner.utils.toFormattedDisplay
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ResultBottomSheetDialog(
    val qrResults: QrResults,
    val contexted: Context,
    val onDismissListener:()->Unit
) : BottomSheetDialogFragment() {
    private var _binding: LayoutQrResultShowBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelperI: DbHelperI
    private var qrResultInitiated: QrResults? = null

    companion object {
        fun ResultBottomSheetDialog.showResultDialog(
            fragmentManager: FragmentManager,
            tag: String = "fk"
        ) {
            show(fragmentManager, tag)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.SheetDialog
        );
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        dbHelperI = DbHelper(LocaleDataBase.getAppDatabase(contexted)!!)
        qrResultInitiated = qrResults
        _binding = LayoutQrResultShowBinding.inflate(inflater)
        showDialog()
        onClicks()
        return binding.root
    }


    private fun onClicks() {
        binding.apply {
            favouriteIcon.setOnClickListener {
                if (it.isSelected) {
                    removeFromFavourite()
                } else
                    addToFavourite()
            }

            copyResult.setOnClickListener {
                copyResultToClipBoard()
            }

            shareResult.setOnClickListener {
                shareResult()
            }

            cancelDialog.setOnClickListener {
                dismiss()
            }
        }
    }


    private fun addToFavourite() {
        binding.apply {
            favouriteIcon.isSelected = true
            dbHelperI.addToFavourite(qrResultInitiated?.id!!)
        }
    }

    private fun removeFromFavourite() {
        binding.apply {
            favouriteIcon.isSelected = false
            dbHelperI.removeFromFavourite(qrResultInitiated?.id!!)
        }
    }

    fun showDialog() {
        binding.apply {
            scannedDate.text = qrResultInitiated?.calendar?.toFormattedDisplay()
            scannedText.text = qrResultInitiated!!.result
            favouriteIcon.isSelected = qrResultInitiated!!.favourite
        }
    }


    private fun copyResultToClipBoard() {
        binding.apply {
            val clipboard = contexted.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("QrCodeScannedResult", scannedText.text)
            clipboard.text = clip.getItemAt(0).text.toString()
            Toast.makeText(contexted, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareResult() {
        binding.apply {
            val txtIntent = Intent(Intent.ACTION_SEND)
            txtIntent.type = "text/plain"
            txtIntent.putExtra(
                Intent.EXTRA_TEXT,
                scannedText.text.toString()
            )
            contexted.startActivity(Intent.createChooser(txtIntent, "Share QR Result"))
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener()
    }

    interface OnDismissListener {
        fun onDismiss()
    }
}