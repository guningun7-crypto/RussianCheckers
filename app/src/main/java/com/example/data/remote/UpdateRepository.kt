package com.example.data.remote

import com.example.domain.curriculum.ContentPackParser
import com.example.domain.curriculum.ParsedContentPack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class UpdateRepository {
  private val client = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(20, TimeUnit.SECONDS)
    .build()

  suspend fun fetchContentPack(url: String): Result<ParsedContentPack> = withContext(Dispatchers.IO) {
    val cleanUrl = url.trim()
    if (!cleanUrl.startsWith("https://") && !cleanUrl.startsWith("http://")) {
      return@withContext Result.failure(
        IllegalArgumentException("Укажите корректную ссылку, начинающуюся с https://")
      )
    }

    return@withContext try {
      val request = Request.Builder()
        .url(cleanUrl)
        .header("Accept", "application/json")
        .build()
      client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
          return@withContext Result.failure(
            IllegalStateException("Сервер вернул ошибку ${response.code}. Проверьте ссылку.")
          )
        }
        val body = response.body?.string()
        if (body.isNullOrBlank()) {
          return@withContext Result.failure(IllegalStateException("Пустой ответ от сервера."))
        }
        ContentPackParser.parse(body)
      }
    } catch (e: Exception) {
      Result.failure(
        IllegalStateException(e.localizedMessage ?: "Не удалось загрузить обновление. Проверьте интернет.")
      )
    }
  }
}
