package com.example.qrcodescanner.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcodescanner.R
import com.example.qrcodescanner.databinding.DialogRemoveAllBinding
import com.example.qrcodescanner.databinding.LayoutSingleItemQrResultBinding
import com.example.qrcodescanner.database.model.QrResults
import com.example.qrcodescanner.roomdb.utils.DbHelperI

import com.example.qrcodescanner.ui.dialog.QrCodeResultDialog
import com.example.qrcodescanner.utils.gone
import com.example.qrcodescanner.utils.toFormattedDisplay
import com.example.qrcodescanner.utils.visible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


class ScannedResultListAdapter(
    var dbHelperI: DbHelperI,
    var context: Context,
    var layoutInflater: LayoutInflater,
    private var listOfScannedResult: MutableList<QrResults>,
    val resultFunction:(QrResults)->Unit
) :
    RecyclerView.Adapter<ScannedResultListAdapter.ScannedResultListViewHolder>() {

    private var resultDialog: QrCodeResultDialog =
        QrCodeResultDialog(context)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScannedResultListViewHolder {
        val binding =
            LayoutSingleItemQrResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        layoutInflater = LayoutInflater.from(parent.context)
        return ScannedResultListViewHolder(binding,resultFunction)
    }

    override fun getItemCount(): Int {
        return listOfScannedResult.size
    }

    override fun onBindViewHolder(holder: ScannedResultListViewHolder, position: Int) {
        holder.bind(listOfScannedResult[position], position)
    }

    inner class ScannedResultListViewHolder(private val binding: LayoutSingleItemQrResultBinding, resultFunction: (QrResults) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(qrResult: QrResults, position: Int) {
            binding.result.text = qrResult.result!!
            binding.tvTime.text = qrResult.calendar.toFormattedDisplay()
            setResultTypeIcon()
            setFavourite(qrResult.favourite)
            onClicks(qrResult, position)
        }

        private fun setResultTypeIcon() {

        }

        private fun setFavourite(isFavourite: Boolean) {
            if (isFavourite)
                binding.favouriteIcon.visible()
            else
                binding.favouriteIcon.gone()
        }


        private fun onClicks(qrResult: QrResults, position: Int) {
            binding.layoutQrResult.setOnClickListener {
                resultFunction(qrResult)

            }

            binding.layoutQrResult.setOnLongClickListener {
                showDeleteDialog(qrResult, position)
                return@setOnLongClickListener true
            }
        }


        private fun showDeleteDialog(qrResult: QrResults, position: Int) {

            BottomSheetDialog(context, R.style.SheetDialog).also { dialog ->
                val binding = DialogRemoveAllBinding.inflate(layoutInflater)
                dialog.setContentView(binding.root)
                dialog.behavior.isDraggable = true
                dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                binding.apply {
                    btnClear.text = context.getString(R.string.delete)
                    title.text = context.getString(R.string.delete)
                    subTitle.text = context.getString(R.string.want_to_delete)
                    cancelDialog.setOnClickListener {
                        dialog.dismiss()

                    }
                    btnClear.setOnClickListener {
                        deleteThisRecord(qrResult, position)
                        dialog.dismiss()
                    }
                }
                dialog.show()
            }
        }

        private fun deleteThisRecord(qrResult: QrResults, position: Int) {
            dbHelperI.deleteQrResult(qrResult.id!!)
            listOfScannedResult.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}