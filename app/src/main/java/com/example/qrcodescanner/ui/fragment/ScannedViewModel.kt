package com.example.qrcodescanner.ui.fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrcodescanner.database.model.QrResults
import com.example.qrcodescanner.database.repository.QrRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScannedViewModel @Inject constructor(private val repository: QrRepository) : ViewModel() {
    private val _record = MutableStateFlow<State>(State.Idle)
    val readRecord = _record.asStateFlow()

    init {
        observeRecord()
    }

    private fun observeRecord() {
        viewModelScope.launch {
            repository.getAllRecord().collect{
                _record.value = State.Success(it)
            }
        }
    }



    sealed class State {
        data class Success(val record: List<QrResults>) : State()
        object Idle : State()
    }
}