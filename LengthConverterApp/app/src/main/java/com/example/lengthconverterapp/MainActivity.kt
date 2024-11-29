package com.example.lengthconverterapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import com.example.lengthconverterapp.ui.theme.LengthConverterAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LengthConverterAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LengthConverterApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun LengthConverterApp(modifier: Modifier = Modifier) {
    var inputValue by remember { mutableStateOf("") }
    var fromUnit by remember { mutableStateOf("Meter") }
    var toUnit by remember { mutableStateOf("Meter") }
    var result by remember { mutableStateOf("") }

    val units = listOf("Meter", "Millimeter", "Mile", "Foot")
    val conversionRates = mapOf(
        "Meter" to 1.0,
        "Millimeter" to 1000.0,
        "Mile" to 0.000621371,
        "Foot" to 3.28084
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = inputValue,
            onValueChange = { inputValue = it },
            label = { Text("Enter value") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenuUnitSelector(
            label = "From",
            options = units,
            selectedOption = fromUnit,
            onOptionSelected = { fromUnit = it }
        )

        DropdownMenuUnitSelector(
            label = "To",
            options = units,
            selectedOption = toUnit,
            onOptionSelected = { toUnit = it }
        )

        Button(
            onClick = {
                if (inputValue.isNotEmpty()) {
                    val value = inputValue.toDouble()
                    result = convertLength(value, fromUnit, toUnit, conversionRates)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Convert")
        }

        Text(text = "Result: $result", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun DropdownMenuUnitSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "$label: $selectedOption")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun convertLength(value: Double, from: String, to: String, rates: Map<String, Double>): String {
    val valueInMeters = value / rates[from]!!
    val result = valueInMeters * rates[to]!!
    return "%.2f".format(result)
}

@Preview(showBackground = true)
@Composable
fun LengthConverterAppPreview() {
    LengthConverterAppTheme {
        LengthConverterApp()
    }
}
