package com.example.qrcodescanner.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcodescanner.database.model.QrResults
import com.example.qrcodescanner.databinding.LayoutSingleItemQrResultBinding
import com.example.qrcodescanner.utils.gone
import com.example.qrcodescanner.utils.toFormattedDisplay
import com.example.qrcodescanner.utils.visible

class ScannedResultAdapter : ListAdapter<QrResults, ScannedResultAdapter.ScannedViewHolder>(
    DiffCallBack()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScannedViewHolder {
        val binding =
            LayoutSingleItemQrResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ScannedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScannedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ScannedViewHolder(private val binding: LayoutSingleItemQrResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QrResults) {
            binding.apply {
                result.text = item.result
                tvTime.text = item.calendar.toFormattedDisplay()
                setFavourite(item.favourite)
            }
        }

        private fun setFavourite(isFavourite: Boolean) {
            if (isFavourite)
                binding.favouriteIcon.visible()
            else
                binding.favouriteIcon.gone()
        }

    }

    class DiffCallBack : DiffUtil.ItemCallback<QrResults>() {
        override fun areItemsTheSame(
            oldItem: QrResults,
            newItem: QrResults
        ) = false

        override fun areContentsTheSame(
            oldItem: QrResults,
            newItem: QrResults
        ) = false
    }
}