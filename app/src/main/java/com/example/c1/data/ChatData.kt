package com.example.c1.data

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ResponseStoppedException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ChatData {
    val api_key = "AIzaSyDKB7OaGcEskE51ddteFffq44zQrVFXfaM"

    suspend fun getResponse(prompt :String):Chat{
        val genrativeModel = GenerativeModel(modelName = "gemini-pro", apiKey = api_key)

        try{
            val response = withContext(Dispatchers.IO){
                genrativeModel.generateContent(prompt)
            }
            return Chat(
                prompt = response.text ?:"error",
                bitmap = null,
                isFromUser = false
            )
        }catch(e: ResponseStoppedException){
            return Chat(
                prompt = "Response stopped",
                bitmap = null,
                isFromUser = false
            )

        }
    }
    suspend fun getResponseWithImage(prompt :String, bitmap: Bitmap):Chat{
        val genrativeModel = GenerativeModel(modelName = "gemini-pro-vision", apiKey = api_key)

        try{
            val inputContent = content{
                image(bitmap)
                text(prompt)
            }
            val response = withContext(Dispatchers.IO){
                genrativeModel.generateContent(inputContent)
            }
            return Chat(
                prompt = response.text ?:"error",
                bitmap = null,
                isFromUser = false
            )
        }catch(e: ResponseStoppedException){
            return Chat(
                prompt = "Response stopped",
                bitmap = null,
                isFromUser = false
            )

        }
    }
}