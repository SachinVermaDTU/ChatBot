package com.example.c1

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.c1.data.Chat
import com.example.c1.data.ChatData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel: ViewModel(){
     private val _chatState = MutableStateFlow(ChatState())
     val chatState  = _chatState.asStateFlow()

    fun onEvent(events: ChatUiEvents){
        when(events){
            is ChatUiEvents.UpdatePrompt -> {
                _chatState.value = _chatState.value.copy(prompt = events.newPromt)
            }
            is ChatUiEvents.SendPrompt -> {
                if(events.prompt.isNotBlank() || events.bitmap != null){
                    addPrompt(events.prompt, events.bitmap)
                    if(events.bitmap != null){
                        getResponseWithImage(events.prompt, events.bitmap)
                    }else{
                        getResponse(events.prompt)
                    }
                }
            }
        }
    }
    private fun addPrompt(prompt : String, bitmap: Bitmap?){
        _chatState.update {
            it.copy(
                chatList = it.chatList.toMutableList().apply {
                    add(0, Chat(prompt = prompt, bitmap = bitmap, isFromUser = true))
                },
                prompt = "",
                bitmap = null
            )
        }
    }

    private fun getResponse(prompt:String){
        viewModelScope.launch {
            val chat = ChatData.getResponse(prompt)
            _chatState.update{
                it.copy(
                    chatList = it.chatList.toMutableList().apply {
                        add(0, chat)
                    }
                )
            }
        }
    }
    private fun getResponseWithImage(prompt:String,bitmap: Bitmap){
        viewModelScope.launch {
            val chat = ChatData.getResponseWithImage(prompt,bitmap)
            _chatState.update{
                it.copy(
                    chatList = it.chatList.toMutableList().apply {
                        add(0, chat)
                    }
                )
            }
        }
    }

}