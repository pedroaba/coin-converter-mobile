package com.pedroaba.coinconversor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedroaba.coinconversor.model.CurrencyType
import com.pedroaba.coinconversor.network.KtorHTTPClient
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

    init {
        viewModelScope.launch {
            _currencyTypes.value = KtorHTTPClient.getCurrencyTypes().mapCatching { result ->
                result.values
            }
        }
    }
}