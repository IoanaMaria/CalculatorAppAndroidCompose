package com.example.calculatorappcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.calculatorappcompose.ui.theme.CalculatorAppComposeTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.*
import java.util.Locale
import android.util.Log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorApp()
        }
    }
}

@Composable
fun CalculatorApp() {
    var displayValue by remember { mutableStateOf("0") }
    var startNewNumber by remember { mutableStateOf(true) }
    var storedValue by remember { mutableStateOf<Double?>(null) }
    var pendingOperator by remember { mutableStateOf<String?>(null) }

    fun enterNumber(number: String) {
        if (startNewNumber || displayValue == "Error") {
            displayValue = number
            startNewNumber = false
        } else if (displayValue.length < 15) {
            displayValue = if (displayValue == "0") {
                number
            } else {
                displayValue + number
            }
        }
    }

    fun performCalculation(
        first: Double,
        second: Double,
        operator: String
    ): Double? {
        val result = when (operator) {
            "+" -> first + second
            "−" -> first - second
            "×" -> first * second
            "÷" -> {
                if (second == 0.0) return null
                first / second
            }
            else -> return null
        }

        return result.takeIf { it.isFinite() }
    }

    fun formatResult(value: Double): String {
        return String.format(Locale.US, "%.8f", value)
            .trimEnd('0')
            .trimEnd('.')
            .let {
                if (it.isEmpty() || it == "-0") "0" else it
            }
    }

    fun showError() {
        displayValue = "Error"
        storedValue = null
        pendingOperator = null
        startNewNumber = true
    }

    fun selectOperator(operator: String) {
        val current = displayValue.toString().toDoubleOrNull()
            ?: return

        val previous = storedValue
        val previousOperator = pendingOperator

        if (previous != null &&
            previousOperator != null &&
            !startNewNumber
        ) {
            val result = performCalculation(
                previous,
                current,
                previousOperator
            )

            if (result == null) {
                showError()
                return
            }

            displayValue = formatResult(result)
            storedValue = result
        } else {
            storedValue = current
        }

        pendingOperator = operator
        startNewNumber = true
    }

    fun enterDecimal() {
        if (startNewNumber || displayValue == "Error") {
            displayValue = "0."
            startNewNumber = false
        } else if (!displayValue.contains(".")) {
            displayValue = "${displayValue}."
        }
    }

    fun calculateResult() {
        val first = storedValue ?: return
        val operator = pendingOperator ?: return
        val second = displayValue.toDoubleOrNull()
            ?: return

        val result = performCalculation(first, second, operator)

        if (result == null) {
            showError()
            return
        }

        displayValue = formatResult(result)
        storedValue = null
        pendingOperator = null
        startNewNumber = true
    }

    fun calculatePercentage() {
        val value = displayValue.toString().toDoubleOrNull()
            ?: return

        displayValue = formatResult(value / 100.0)
        startNewNumber = true
    }

    fun changeSign() {
        val value = displayValue.toString()

        if (value == "Error" || value == "0") return

        displayValue = if (value.startsWith("-")) {
            value.drop(1)
        } else {
            "-$value"
        }
    }

    fun deleteLastCharacter() {
        if (startNewNumber || displayValue == "Error") return

        val value = displayValue.toString().dropLast(1)

        displayValue = when (value) {
            "", "-" -> "0"
            else -> value
        }
    }

    fun clear() {
        displayValue = "0"
        storedValue = null
        pendingOperator = null
        startNewNumber = true
    }


    Column {
        Text(
            text = displayValue,
            fontSize = 40.sp
        )

        Row {
            CalculatorButton("AC", onClick = { clear() })
            CalculatorButton("±", onClick = { changeSign() })
            CalculatorButton("%", onClick = { calculatePercentage() })
            CalculatorButton("÷", onClick = { selectOperator("÷") })
        }

        Row {
            CalculatorButton("7", onClick = { enterNumber("7") })
            CalculatorButton("8", onClick = { enterNumber("8") })
            CalculatorButton("9", onClick = { enterNumber("9") })
            CalculatorButton("×", onClick = { selectOperator("×") })
        }

        Row {
            CalculatorButton("4", onClick = { enterNumber("4") })
            CalculatorButton("5", onClick = { enterNumber("5") })
            CalculatorButton("6", onClick = { enterNumber("6") })
            CalculatorButton("−", onClick = { selectOperator("−") })
        }

        Row {
            CalculatorButton("1", onClick = {enterNumber("1")})
            CalculatorButton("2", onClick = {enterNumber("2")})
            CalculatorButton("3", onClick = {enterNumber("3")})
            CalculatorButton("+", onClick = {selectOperator("+")})
        }

        Row {
            CalculatorButton("DEL", onClick = {deleteLastCharacter()})
            CalculatorButton("0", onClick = {enterNumber("0")})
            CalculatorButton(".", onClick = {enterDecimal()})
            CalculatorButton("=", onClick = {calculateResult()})
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    onClick: () -> Unit
) {
    Button(onClick = onClick) {
        Text(text)
    }
}