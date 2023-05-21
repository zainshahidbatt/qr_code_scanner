package com.example.qrcodescanner.roomdb.utils

import com.example.qrcodescanner.database.model.QrResults
import kotlinx.coroutines.flow.Flow


interface DbHelperI {

    fun insertQRResult(result: String): Int

    fun getQRResult(id: Int): QrResults

    fun addToFavourite(id: Int): Int

    fun removeFromFavourite(id: Int): Int

    fun deleteQrResults(id: Int): Int

    fun deleteQrResult(id: Int): Int

    fun getAllQRScannedResult(): Flow<List<QrResults>>

    fun getAllFavouriteQRScannedResult(): Flow<List<QrResults>>

    fun deleteAllQRScannedResult()

    fun deleteAllFavouriteQRScannedResult()
}