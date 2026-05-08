package com.example.aipersonal

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NvidiaClient {
    private const val BASE_URL = "https://integrate.api.nvidia.com/"
    private const val TEXT_MODEL = "google/gemma-4-31b-it"
    private const val IMAGE_MODEL = "black-forest-labs/flux.2-klein-4b"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()

    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()
        .create(NvidiaNimApi::class.java)

    suspend fun generateResponse(apiKey: String, history: List<Message>): String? {
        val authHeader = "Bearer $apiKey"
        val request = ChatRequest(
            model = TEXT_MODEL,
            messages = history
        )

        return try {
            val response = api.getCompletion(authHeader, request)
            if (response.isSuccessful) {
                response.body()?.choices?.firstOrNull()?.message?.content
            } else {
                "Error: ${response.code()} - ${response.message()}"
            }
        } catch (e: Exception) {
            "Failure: ${e.message}"
        }
    }

    suspend fun generateImage(apiKey: String, prompt: String): String? {
        val authHeader = "Bearer $apiKey"
        val request = ImageRequest(prompt = prompt)
        
        // Endpoint spesifik untuk model image dari NVIDIA NIM
        val imageUrl = "https://ai.api.nvidia.com/v1/genai/$IMAGE_MODEL"

        return try {
            val response = api.generateImage(url = imageUrl, authHeader = authHeader, request = request)
            if (response.isSuccessful) {
                response.body()?.artifacts?.firstOrNull()?.base64
            } else {
                val errBody = response.errorBody()?.string() ?: ""
                null.also { android.util.Log.e("NvidiaClient", "Image error ${response.code()}: $errBody") }
            }
        } catch (e: Exception) {
            android.util.Log.e("NvidiaClient", "Image exception: ${e.message}")
            null
        }
    }
}
