package com.example.qrcodescanner.ui.dialog

import androidx.fragment.app.Fragment
import com.example.qrcodescanner.R
import com.example.qrcodescanner.databinding.DialogRemoveAllBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Fragment.showHistoryUtilDialog(
    titleText: String,
    subtitle: String,
    buttonText: String,
    unit: () -> Unit
) {

    BottomSheetDialog(requireContext(), R.style.SheetDialog).also { dialog ->
        val binding = DialogRemoveAllBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.behavior.isDraggable = true
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.apply {
            btnClear.text = buttonText
            title.text = titleText
            subTitle.text = subtitle
            cancelDialog.setOnClickListener {
                dialog.dismiss()

            }
            btnClear.setOnClickListener {
                unit.invoke()
                dialog.dismiss()
            }
        }
        dialog.show()
    }


}