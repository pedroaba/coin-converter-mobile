package com.pedroaba.coinconversor

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<CurrencyExchangeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            viewModel.currencyTypes.collect { result ->
                result.onSuccess {
                    Toast.makeText(this@MainActivity, it.size.toString(), Toast.LENGTH_LONG).show()
                }.onFailure {
                    Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}