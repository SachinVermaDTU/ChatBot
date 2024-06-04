package com.example.c1

import android.graphics.Bitmap
import com.example.c1.data.Chat

data class ChatState(
    val chatList: MutableList<Chat> = mutableListOf(),
    val prompt: String = "",
    val bitmap: Bitmap? = null
    )
