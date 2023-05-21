package com.example.qrcodescanner.ui.dialog

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.text.ClipboardManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.qrcodescanner.R
import com.example.qrcodescanner.database.model.QrResults
import com.example.qrcodescanner.roomdb.utils.DbHelper
import com.example.qrcodescanner.roomdb.utils.DbHelperI
import com.example.qrcodescanner.database.LocaleDataBase
import com.example.qrcodescanner.utils.toFormattedDisplay
import com.google.android.material.bottomsheet.BottomSheetDialog

class QrCodeResultDialog(var context: Context) {

    private var dialog = BottomSheetDialog(context, R.style.SheetDialog)

    private lateinit var dbHelperI: DbHelperI

    private var qrResult: QrResults? = null


    private val favouriteIcon = dialog.findViewById<ImageView>(R.id.favourite_icon)
    val copyResult = dialog.findViewById<ImageView>(R.id.copy_result)
    val shareResult = dialog.findViewById<ImageView>(R.id.share_result)
    val cancelDialog = dialog.findViewById<ImageView>(R.id.cancel_dialog)
    val scannedDate = dialog.findViewById<TextView>(R.id.scanned_date)
    val scannedText = dialog.findViewById<TextView>(R.id.scanned_text)


    private var onDismissListener: OnDismissListener? = null

    init {
        init()
        initDialog()
    }


    private fun init() {
        dbHelperI = DbHelper(LocaleDataBase.getAppDatabase(context)!!)
    }

    private fun initDialog() {
        dialog = BottomSheetDialog(context, R.style.SheetDialog)
        dialog.setContentView(R.layout.layout_qr_result_show)
        dialog.setCancelable(true)
        onClicks()
    }

    private fun onClicks() {

        favouriteIcon?.setOnClickListener {
            if (it.isSelected) {
                removeFromFavourite()
            } else
                addToFavourite()
        }

        copyResult?.setOnClickListener {
            copyResultToClipBoard()
        }

        shareResult?.setOnClickListener {
            shareResult()
        }

        cancelDialog?.setOnClickListener {
            dialog.dismiss()
            onDismissListener?.onDismiss()
        }
    }


    private fun addToFavourite() {
        favouriteIcon?.isSelected = true
        dbHelperI.addToFavourite(qrResult?.id!!)
    }

    private fun removeFromFavourite() {
        favouriteIcon?.isSelected = false
        dbHelperI.removeFromFavourite(qrResult?.id!!)
    }

    fun show(recentQrResults: QrResults) {
        this.qrResult = recentQrResults
        scannedDate?.text = qrResult?.calendar?.toFormattedDisplay()
        scannedText?.text = qrResult!!.result
        favouriteIcon?.isSelected = qrResult!!.favourite
        dialog.show()
    }

    fun setOnDismissListener(dismissListener: OnDismissListener) {
        this.onDismissListener = dismissListener
    }

    private fun copyResultToClipBoard() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("QrCodeScannedResult", scannedText?.text)
        clipboard.text = clip.getItemAt(0).text.toString()
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun shareResult() {
        val txtIntent = Intent(Intent.ACTION_SEND)
        txtIntent.type = "text/plain"
        txtIntent.putExtra(
            Intent.EXTRA_TEXT,
            scannedText?.text.toString()
        )
        context.startActivity(Intent.createChooser(txtIntent, "Share QR Result"))
    }

    interface OnDismissListener {
        fun onDismiss()
    }
}