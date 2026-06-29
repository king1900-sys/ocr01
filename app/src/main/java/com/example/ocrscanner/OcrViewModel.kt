package com.example.ocrscanner

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ocrscanner.data.OcrDatabase
import com.example.ocrscanner.data.OcrRecord
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class OcrViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = OcrDatabase.getDatabase(application).ocrDao()
    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing
    val allRecords: StateFlow<List<OcrRecord>> = dao.getAllRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val recognizer = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())

    fun recognize(bitmap: Bitmap, source: String) {
        viewModelScope.launch {
            _isProcessing.value = true
            _recognizedText.value = ""
            try {
                val text = withContext(Dispatchers.Default) {
                    recognizer.process(InputImage.fromBitmap(bitmap, 0)).await().textBlocks.joinToString("\n") { it.text }
                }
                _recognizedText.value = text.ifEmpty { "No text found" }
                dao.insert(OcrRecord(recognizedText = _recognizedText.value, sourceType = source))
            } catch (e: Exception) {
                _recognizedText.value = "Error: ${e.message}"
            }
            _isProcessing.value = false
        }
    }

    fun toggleFavorite(record: OcrRecord) {
        viewModelScope.launch { dao.update(record.copy(isFavorite = !record.isFavorite)) }
    }

    fun deleteRecord(record: OcrRecord) {
        viewModelScope.launch { dao.delete(record) }
    }

    fun deleteAllRecords() {
        viewModelScope.launch { dao.deleteAll() }
    }
}
