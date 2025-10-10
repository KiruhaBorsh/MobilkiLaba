package com.example.mobilkilaba

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobilkilaba.ui.theme.MobilkiLabaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobilkiLabaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DotProductScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun DotProductScreen(modifier: Modifier = Modifier) {
    var sizeText by remember { mutableStateOf("5") }
    var vectorAText by remember { mutableStateOf("") }
    var vectorBText by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }

    val vectorMath = remember { VectorMath() }

    Column(modifier = modifier.padding(16.dp)) {
        Text(text = "Размерность вектора (целое > 0):")
        OutlinedTextField(
            value = sizeText,
            onValueChange = { sizeText = it.filter { ch -> ch.isDigit() } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            label = { Text("Размерность") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {
            val n = sizeText.toIntOrNull()
            if (n == null || n <= 0) {
                resultText = "Введите корректную размерность (>0)"
                return@Button
            }
            val a = vectorMath.generateRandomVector(n)
            val b = vectorMath.generateRandomVector(n)
            val dot = vectorMath.dotProduct(a, b)
            vectorAText = a.joinToString(prefix = "[", postfix = "]", separator = ", ")
            vectorBText = b.joinToString(prefix = "[", postfix = "]", separator = ", ")
            resultText = "Скалярное произведение: $dot"
        }) {
            Text(text = "Сгенерировать и вычислить")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Вектор A:")
        Text(text = vectorAText)

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Вектор B:")
        Text(text = vectorBText)

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = resultText)
    }
}

@Preview(showBackground = true)
@Composable
fun DotProductPreview() {
    MobilkiLabaTheme {
        DotProductScreen()
    }
}