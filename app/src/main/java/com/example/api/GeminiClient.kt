package com.example.api

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val apiService: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    /**
     * Converts a Bitmap to base64 string.
     */
    private fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    /**
     * Runs OCR Chat Extraction on a chat screenshot.
     */
    suspend fun extractChatFromImage(bitmap: Bitmap): List<ExtractedMessage> {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "API Key is missing or default. Please configure via AI Studio Secrets Panel.")
            return emptyList()
        }

        val base64Image = bitmap.toBase64()

        val prompt = """
            You are a precise OCR and chat logs parser.
            Analyze this screenshot of a chat application (such as WhatsApp) and extract the conversation.
            Return ONLY a valid JSON array of objects, where each object has these exact fields:
            - "sender": string (the name of the contact, or "Me" if the message is outgoing/on the right side)
            - "text": string (the exact text of the message)
            - "timestamp": string (optional, any time string visible next to the message, e.g. "10:34 AM")
            
            Do not wrap the JSON in markdown code blocks like ```json ... ```. Return ONLY raw JSON text.
            If the image has no recognizable chat text, return an empty array [].
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = prompt),
                        Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Image))
                    )
                )
            ),
            generationConfig = GenerationConfig(
                responseMimeType = "application/json",
                temperature = 0.2f
            )
        )

        return try {
            val response = apiService.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            Log.d(TAG, "Raw Gemini Response: $jsonText")

            if (jsonText != null) {
                val listType = Types.newParameterizedType(List::class.java, ExtractedMessage::class.java)
                val adapter = moshi.adapter<List<ExtractedMessage>>(listType)
                adapter.fromJson(jsonText) ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in extractChatFromImage: ${e.message}", e)
            emptyList()
        }
    }
}
