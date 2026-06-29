package com.example.ocrscanner.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OcrDao {
    @Query("SELECT * FROM ocr_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<OcrRecord>>
    @Insert
    suspend fun insert(record: OcrRecord): Long
    @Update
    suspend fun update(record: OcrRecord)
    @Delete
    suspend fun delete(record: OcrRecord)
    @Query("DELETE FROM ocr_records")
    suspend fun deleteAll()
}
