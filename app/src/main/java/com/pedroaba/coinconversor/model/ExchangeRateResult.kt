package com.pedroaba.coinconversor.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@InternalSerializationApi @Serializable
data class ExchangeRateResult(
    val from: String,
    val to: String,

    @SerialName("exchange_rate")
    val exchangeRate: Double,
)
