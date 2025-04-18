package com.pedroaba.coinconversor.network.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@InternalSerializationApi @Serializable
data class CurrencyType(
    val name: String,
    val acronym: String,
    val symbol: String,

    @SerialName("country_flag_image_url")
    val countryFlagImageUrl: String,
)

@InternalSerializationApi @Serializable
data class CurrencyTypeResult(
    val values: List<CurrencyType>,
)