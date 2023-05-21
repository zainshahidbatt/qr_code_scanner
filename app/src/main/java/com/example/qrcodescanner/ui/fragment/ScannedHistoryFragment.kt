package com.example.qrcodescanner.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrcodescanner.R
import com.example.qrcodescanner.database.LocaleDataBase
import com.example.qrcodescanner.database.model.QrResults
import com.example.qrcodescanner.databinding.FragmentScannedHistoryBinding
import com.example.qrcodescanner.roomdb.utils.DbHelper
import com.example.qrcodescanner.roomdb.utils.DbHelperI
import com.example.qrcodescanner.ui.adapter.ScannedResultAdapter
import com.example.qrcodescanner.ui.adapter.ScannedResultListAdapter
import com.example.qrcodescanner.ui.base.BaseFragment
import com.example.qrcodescanner.ui.dialog.ResultBottomSheetDialog
import com.example.qrcodescanner.ui.dialog.ResultBottomSheetDialog.Companion.showResultDialog
import com.example.qrcodescanner.ui.dialog.showHistoryUtilDialog
import com.example.qrcodescanner.ui.fragment.ScannedHistoryFragment.ResultListType.ALL_RESULT
import com.example.qrcodescanner.ui.fragment.ScannedHistoryFragment.ResultListType.FAVOURITE_RESULT
import com.example.qrcodescanner.utils.gone
import com.example.qrcodescanner.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable


@Suppress("DEPRECATION")
@AndroidEntryPoint
class ScannedHistoryFragment : BaseFragment<FragmentScannedHistoryBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentScannedHistoryBinding =
        FragmentScannedHistoryBinding::inflate

    enum class ResultListType : Serializable {
        ALL_RESULT, FAVOURITE_RESULT
    }

    companion object {

        private const val ARGUMENT_RESULT_LIST_TYPE = "ArgumentResultType"

        fun newInstance(screenType: ResultListType): ScannedHistoryFragment {
            val bundle = Bundle()
            bundle.putSerializable(ARGUMENT_RESULT_LIST_TYPE, screenType)
            val fragment = ScannedHistoryFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val viewModel by viewModels<ScannedViewModel>()

    private lateinit var dbHelperI: DbHelperI

    private var resultListType: ResultListType? = null

    private var resultBottomSheetDialog: ResultBottomSheetDialog? = null

    private val mAdapter = ScannedResultAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleArguments()
    }

    private fun handleArguments() {
        resultListType = arguments?.getSerializable(ARGUMENT_RESULT_LIST_TYPE) as ResultListType
    }


    override fun onCreatedView() {
        init()
        setSwipeRefresh()
        onClicks()
        observer()
        showListOfResults()
    }

    private fun init() {
//        viewModel.getContext(requireContext())
//        viewModel.getAppDatabase()
        dbHelperI = DbHelper(LocaleDataBase.getAppDatabase(requireContext())!!)
        binding.layoutHeader.tvHeaderText.text = getString(R.string.recent_scanned_results)
    }

    private fun showListOfResults() {
        when (resultListType) {
            ALL_RESULT -> showAllResults()
            FAVOURITE_RESULT -> showFavouriteResults()
            null -> {
                Log.d("jeje", "show list of results is empty")
            }
        }
    }

    private fun observer() {
        lifecycleScope.launchWhenCreated {
            viewModel.readRecord.collect { state ->
                when (state) {
                    is ScannedViewModel.State.Success -> {
                        binding.scannedHistoryRecyclerView.adapter = mAdapter
                        mAdapter.submitList(state.record)
                    }

                    else -> {}
                }

            }
        }
    }

    private fun showAllResults() {
//        val listOfAllResult = dbHelperI.getAllQRScannedResult()
//        val listOfAllResult = viewModel.getAllQr()
//        showResults(listOfAllResult)
        binding.layoutHeader.tvHeaderText.text = getString(R.string.recent_scanned)
    }

    private fun showFavouriteResults() {
        //  val listOfFavouriteResult = dbHelperI.getAllFavouriteQRScannedResult()
//        val listOfFavouriteResult = viewModel.getAllFavourite()
//        showResults(listOfFavouriteResult)
        binding.layoutHeader.tvHeaderText.text = getString(R.string.favourites_scanned_results)
    }


    private fun showResults(listOfQrResult: List<QrResults>) {
        if (listOfQrResult.isNotEmpty())
            initRecyclerView(listOfQrResult)
        else
            showEmptyState()
    }

    /////////--------------------------------------------///////
    private fun initRecyclerView(listOfQrResult: List<QrResults>) {
        binding.scannedHistoryRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.scannedHistoryRecyclerView.adapter =
            ScannedResultListAdapter(
                dbHelperI,
//                viewModel.dbHelperI,
                requireContext(),
                layoutInflater,
                listOfQrResult.toMutableList()
            ) {
                resultBottomSheetDialog =
                    ResultBottomSheetDialog(qrResults = it, requireContext()) {}
                resultBottomSheetDialog?.showResultDialog(childFragmentManager)
            }
        showRecyclerView()
    }

    private fun setSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = false
            showListOfResults()
        }
    }


    private fun onClicks() {
        binding.layoutHeader.removeAll.setOnClickListener {
            showRemoveAllScannedResultDialog()
        }
    }

    private fun showRemoveAllScannedResultDialog() {
        showHistoryUtilDialog(
            requireContext().getString(R.string.clear_all),
            requireContext().getString(R.string.clear_all_result),
            "Clear"

        ) { clearAllRecords() }
//        BottomSheetDialog(requireContext(), R.style.SheetDialog).also { dialog ->
//            val binding = DialogRemoveAllBinding.inflate(layoutInflater)
//            dialog.setContentView(binding.root)
//            dialog.behavior.isDraggable = true
//            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            binding.apply {
//                title.text = context?.getString(R.string.clear_all)
//                subTitle.text = context?.getString(R.string.clear_all_result)
//                cancelDialog.setOnClickListener {
//                    dialog.dismiss()
//                }
//                btnClear.setOnClickListener {
//                    clearAllRecords()
//                    dialog.dismiss()
//                }
//            }
//            dialog.show()
//        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun clearAllRecords() {
        when (resultListType) {
            ALL_RESULT ->
                //viewModel.deleteAllResult()
                dbHelperI.deleteAllQRScannedResult()

            FAVOURITE_RESULT ->
                //viewModel.deleteAllFavourite()
                dbHelperI.deleteAllFavouriteQRScannedResult()

            null -> {
                Log.d("jeje", "clear all records are empty")
            }
        }
        binding.scannedHistoryRecyclerView.adapter?.notifyDataSetChanged()
        showListOfResults()
    }

    private fun showRecyclerView() {
        binding.layoutHeader.removeAll.visible()
        binding.scannedHistoryRecyclerView.visible()
        binding.noResultFound.gone()
    }

    private fun showEmptyState() {
        binding.layoutHeader.removeAll.gone()
        binding.scannedHistoryRecyclerView.gone()
        binding.noResultFound.visible()
    }

    override fun onResume() {
        super.onResume()
        showListOfResults()
    }
}
