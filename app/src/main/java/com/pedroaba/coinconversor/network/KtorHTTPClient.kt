package com.pedroaba.coinconversor.network

import com.pedroaba.coinconversor.model.CurrencyTypeResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
object KtorHTTPClient {
    private const val BASE_URL = "http://10.0.2.2:3000"

    private val client = HttpClient(Android) {
        install(Logging)
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun getCurrencyTypes(): Result<CurrencyTypeResult> {
        return requireGet(apiPath = "currency-types")
    }

    private suspend inline fun <reified T> requireGet(apiPath: String): Result<T> {
        return try {
            Result.success(client.get("$BASE_URL/$apiPath").body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
