package com.example.qrcodescanner.roomdb.utils

import com.example.qrcodescanner.database.LocaleDataBase
import com.example.qrcodescanner.database.model.QrResults
import kotlinx.coroutines.flow.Flow
import java.util.*

class DbHelper(var qrResultDataBase: LocaleDataBase) : DbHelperI {

    override fun insertQRResult(result: String): Int {
        val time = Calendar.getInstance()
        val resultType = findResultType(result)
        val qrResult = QrResults(result = result, resultType = resultType, calendar = time, favourite = false)
        return qrResultDataBase.getQrDao().insertQrResults(qrResult).toInt()
    }

    override fun getQRResult(id: Int): QrResults {
        return qrResultDataBase.getQrDao().getQrResults(id)
    }

    override fun addToFavourite(id: Int): Int {
        return qrResultDataBase.getQrDao().addToFavourite(id)
    }

    override fun removeFromFavourite(id: Int): Int {
        return qrResultDataBase.getQrDao().removeFromFavourite(id)
    }

    override fun deleteQrResults(id: Int): Int {
        return qrResultDataBase.getQrDao().deleteQrResult(id)
    }


    override fun getAllQRScannedResult(): Flow<List<QrResults>> {
        return qrResultDataBase.getQrDao().getAllScannedResult()
    }

    override fun getAllFavouriteQRScannedResult(): Flow<List<QrResults>> {
        return qrResultDataBase.getQrDao().getAllFavouriteResult()
    }

    override fun deleteAllQRScannedResult() {
        qrResultDataBase.getQrDao().deleteAllScannedResult()
    }

    override fun deleteAllFavouriteQRScannedResult() {
        qrResultDataBase.getQrDao().deleteAllFavouriteResult()
    }

    /*
    * This feature will add in future
    * */
    private fun findResultType(result: String): String {
        return "TEXT"
    }


    override fun deleteQrResult(id: Int): Int {
        return qrResultDataBase.getQrDao().deleteQrResult(id)
    }


}
