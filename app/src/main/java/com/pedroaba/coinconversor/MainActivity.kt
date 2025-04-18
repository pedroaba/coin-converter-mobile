package com.pedroaba.coinconversor

import android.icu.text.DecimalFormat
import android.icu.text.DecimalFormatSymbols
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.pedroaba.coinconversor.databinding.ActivityMainBinding
import com.pedroaba.coinconversor.network.model.CurrencyType
import com.pedroaba.coinconversor.ui.CurrencyTypeAdapter
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import java.util.Locale

@OptIn(InternalSerializationApi::class)
class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<CurrencyExchangeViewModel>()
    private lateinit var binding: ActivityMainBinding

    private var exchangeRate: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.requireCurrencyTypes()
        binding.etFromExchangeValue.addCurrencyMask()

        lifecycleScope.apply {
            launch {
                viewModel.currencyTypes.collect { result ->
                    result.onSuccess {
                        binding.configureCurrencyTypesSpinners(currencyTypes = it)
                    }.onFailure {
                        Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

            launch {
                viewModel.exchangeRate.collect { result ->
                    result.onSuccess {
                        it?.let {
                            exchangeRate = it.exchangeRate
                            binding.generateConvertedValue()
                        }
                    }.onFailure {
                        Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun ActivityMainBinding.configureCurrencyTypesSpinners(currencyTypes: List<CurrencyType>) {
        spnFromExchange.apply {
            adapter = CurrencyTypeAdapter(currencyTypes)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    val from = currencyTypes[position]
                    val to = currencyTypes[spnToExchange.selectedItemPosition]

                    tvFromCurrencySymbol.text = from.symbol

                    viewModel.requireExchangeRate(
                        from = from.acronym, to = to.acronym
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }


        spnToExchange.apply {
            adapter = CurrencyTypeAdapter(currencyTypes)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    val from = currencyTypes[spnFromExchange.selectedItemPosition]
                    val to = currencyTypes[position]

                    tvToCurrencySymbol.text = to.symbol

                    viewModel.requireExchangeRate(
                        from = from.acronym, to = to.acronym
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    currencyTypes.firstOrNull()?.let {
                        tvFromCurrencySymbol.text = it.symbol
                        tvToCurrencySymbol.text = it.symbol

                        viewModel.requireExchangeRate(
                            from = it.acronym, to = it.acronym
                        )
                    }
                }
            }
        }
    }

    private fun EditText.addCurrencyMask() {
        addTextChangedListener(object : TextWatcher {
            private var currentText = ""

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != currentText) {
                    removeTextChangedListener(this)

                    val cleanedString = s.toString().replace("[,.]".toRegex(), "")
                    val currencyValue = cleanedString.toDoubleOrNull() ?: 0.0

                    val formattedValue = DecimalFormat(
                        "#,##0.00", DecimalFormatSymbols(Locale.getDefault())
                    ).format(currencyValue / 100)

                    currentText = formattedValue
                    setText(formattedValue)
                    setSelection(formattedValue.length)

                    binding.generateConvertedValue()

                    addTextChangedListener(this)
                }
            }
        })
    }

    private fun ActivityMainBinding.generateConvertedValue() {
        exchangeRate?.let {
            val cleanedString = etFromExchangeValue.text.toString().replace("[,.]".toRegex(), "")
            val currencyValue = cleanedString.toDoubleOrNull() ?: 0.0

            val formattedValue = DecimalFormat(
                "#,##0.00",
                DecimalFormatSymbols(Locale.getDefault())
            ).format((currencyValue * exchangeRate!!) / 100)

            tvToExchangeValue.text = formattedValue
        }
    }
}