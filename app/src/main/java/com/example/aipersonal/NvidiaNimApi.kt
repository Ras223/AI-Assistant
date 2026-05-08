package com.example.aipersonal

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

data class ChatRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double = 0.5,
    val max_tokens: Int = 1024,
    val stream: Boolean = false
)

data class Message(
    val role: String,
    val content: String
)

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

data class ImageRequest(
    val prompt: String,
    val seed: Int = 0,
    val steps: Int = 4
)

data class ImageResponse(
    val artifacts: List<Artifact>? = null
)

data class Artifact(
    val base64: String? = null
)

interface NvidiaNimApi {
    @POST("v1/chat/completions")
    suspend fun getCompletion(
        @Header("Authorization") authHeader: String,
        @Body request: ChatRequest
    ): Response<ChatResponse>

    @POST
    suspend fun generateImage(
        @Url url: String,
        @Header("Authorization") authHeader: String,
        @Header("Accept") acceptHeader: String = "application/json",
        @Body request: ImageRequest
    ): Response<ImageResponse>
}
