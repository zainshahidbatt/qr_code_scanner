package com.example.qrcodescanner.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.qrcodescanner.database.model.QrResults

@Database(entities = [QrResults::class], version = 1,exportSchema = false)
abstract class LocaleDataBase : RoomDatabase()  {
    abstract fun getQrDao(): QrResultsDao

    companion object {
        private const val DB_NAME = "MovieStack"
        private var qrResultDataBase: LocaleDataBase? = null
        fun getAppDatabase(context: Context): LocaleDataBase? {
            if (qrResultDataBase == null) {
                qrResultDataBase =
                    Room.databaseBuilder(context.applicationContext, LocaleDataBase::class.java, DB_NAME)
                        .allowMainThreadQueries()
                        .build()
            }
            return qrResultDataBase
        }

        fun destroyInstance() {
            qrResultDataBase = null
        }
    }
}