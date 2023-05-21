package com.example.qrcodescanner.database.repository

import com.example.qrcodescanner.database.QrResultsDao
import com.example.qrcodescanner.database.model.QrResults
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject

class QrRepository @Inject constructor(private val qrResultsDao: QrResultsDao) {

    fun insertRecord(result: String): Int {
        val time = Calendar.getInstance()
        val resultType = findResultType(result)
        val qrResult =
            QrResults(result = result, resultType = resultType, calendar = time, favourite = false)
        return qrResultsDao.insertQrResults(qrResult).toInt()
    }


    fun getAllRecord(): Flow<List<QrResults>> {
        return qrResultsDao.getAllScannedResult()
    }

    fun getAllFavourite(): Flow<List<QrResults>> {
        return qrResultsDao.getAllFavouriteResult()
    }

    fun deleteAllScannedResult() {
        return qrResultsDao.deleteAllScannedResult()
    }

    fun deleteAllFavourite() {
        return qrResultsDao.deleteAllFavouriteResult()
    }

    fun getQRResult(id: Int): QrResults {
        return qrResultsDao.getQrResults(id)
    }

    fun addToFavourite(id: Int): Int {
        return qrResultsDao.addToFavourite(id)
    }

    fun removeFromFavourite(id: Int): Int {
        return qrResultsDao.removeFromFavourite(id)
    }

    fun deleteQrResults(id: Int): Int {
        return qrResultsDao.deleteQrResult(id)
    }


    fun getAllQRScannedResult(): Flow<List<QrResults>> {
        return qrResultsDao.getAllScannedResult()
    }

    fun getAllFavouriteQRScannedResult(): Flow<List<QrResults>> {
        return qrResultsDao.getAllFavouriteResult()
    }

    fun deleteAllQRScannedResult() {
        qrResultsDao.deleteAllScannedResult()
    }

    fun deleteAllFavouriteQRScannedResult() {
        qrResultsDao.deleteAllFavouriteResult()
    }

    /*
    * This feature will add in future
    * */
    private fun findResultType(result: String): String {
        return "TEXT"
    }


    fun deleteQrResult(id: Int): Int {
        return qrResultsDao.deleteQrResult(id)
    }


}