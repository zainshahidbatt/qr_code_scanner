package com.example.qrcodescanner.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.qrcodescanner.database.model.QrResults
import kotlinx.coroutines.flow.Flow

@Dao
interface QrResultsDao {

    @Query("SELECT * FROM QrResults ORDER BY time DESC")
    fun getAllScannedResult(): Flow<List<QrResults>>

    @Query("SELECT * FROM QrResults WHERE favourite = 1 ORDER BY time DESC")
    fun getAllFavouriteResult(): Flow<List<QrResults>>

    @Query("DELETE FROM QrResults")
    fun deleteAllScannedResult()

    @Query("DELETE FROM QrResults WHERE favourite = 1")
    fun deleteAllFavouriteResult()

    @Query("DELETE FROM QrResults WHERE id = :id")
    fun deleteQrResults(id: Int): Int

    @Query("DELETE FROM QrResults WHERE id = :id")
    fun deleteQrResult(id: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQrResults(qrResult: QrResults): Long

    @Query("SELECT * FROM QrResults WHERE id = :id")
    fun getQrResults(id: Int): QrResults

    @Query("UPDATE QrResults SET favourite = 1 WHERE id = :id")
    fun addToFavourite(id: Int): Int

    @Query("UPDATE QrResults SET favourite = 0 WHERE id = :id")
    fun removeFromFavourite(id: Int): Int

    @Query("SELECT * FROM QrResults WHERE result = :result ")
    fun checkIfQrResultsExist(result: String): Int


}