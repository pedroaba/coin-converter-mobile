package com.pedroaba.coinconversor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedroaba.coinconversor.network.model.CurrencyType
import com.pedroaba.coinconversor.network.model.ExchangeRateResult
import com.pedroaba.coinconversor.network.KtorHTTPClient
import com.pedroaba.coinconversor.utils.CurrencyTypeAcronym
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
class CurrencyExchangeViewModel : ViewModel() {
    private val _currencyTypes =
        MutableStateFlow<Result<List<CurrencyType>>>(Result.success(emptyList()))

    val currencyTypes: StateFlow<Result<List<CurrencyType>>> = _currencyTypes.asStateFlow()

    private val _exchangeRate =
        MutableStateFlow<Result<ExchangeRateResult?>>(Result.success(null))

    val exchangeRate: StateFlow<Result<ExchangeRateResult?>> = _exchangeRate.asStateFlow()

    fun requireCurrencyTypes() {
        viewModelScope.launch {
            _currencyTypes.value = KtorHTTPClient.getCurrencyTypes().mapCatching { result ->
                result.values
            }
        }
    }

    fun requireExchangeRate(from: CurrencyTypeAcronym, to: CurrencyTypeAcronym) {
        if (from == to) {
            _exchangeRate.value = Result.success(
                ExchangeRateResult(
                    from = from,
                    to = to,
                    exchangeRate = 1.0
                )
            )
            return
        }

        viewModelScope.launch {
            _exchangeRate.value = KtorHTTPClient.getCurrencyExchange(from, to)
        }
    }
}