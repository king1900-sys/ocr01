package com.example.ocrscanner.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ocr_records")
data class OcrRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recognizedText: String,
    val sourceType: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
