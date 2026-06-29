package com.example.ocrscanner.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [OcrRecord::class], version = 1, exportSchema = false)
abstract class OcrDatabase : RoomDatabase() {
    abstract fun ocrDao(): OcrDao
    companion object {
        @Volatile private var INSTANCE: OcrDatabase? = null
        fun getDatabase(context: Context): OcrDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, OcrDatabase::class.java, "ocr_db").build().also { INSTANCE = it }
            }
    }
}
