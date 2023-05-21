package com.example.qrcodescanner.database.di

import android.content.Context
import androidx.room.Room
import com.example.qrcodescanner.database.QrResultsDao
import com.example.qrcodescanner.database.LocaleDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Singleton
    @Provides
    fun provideRecordDB(@ApplicationContext context: Context): LocaleDataBase {
        return Room.databaseBuilder(context, LocaleDataBase::class.java, "RecordDB")
            .fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun getDao(localeDataBase: LocaleDataBase): QrResultsDao {
        return localeDataBase.getQrDao()
    }

}