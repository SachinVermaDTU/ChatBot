package com.example.c1

import android.graphics.Bitmap

sealed class ChatUiEvents {
 data class UpdatePrompt(val newPromt:String) : ChatUiEvents()
 data class SendPrompt(val prompt: String,
     val bitmap: Bitmap?) : ChatUiEvents()
}
