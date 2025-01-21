package com.example.hackingandroid

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hackingandroid.ui.theme.HackingAndroidTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.Socket
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HackingAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Greeting("Android")
                        MyButtons()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(
        text = "Hello $name!",
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun MyButtons() {
    val context = LocalContext.current
    var clickCount by remember { mutableStateOf(0) }
    var fetchedText by remember { mutableStateOf("Click 'Fetch Data' to load content") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Button for click counting and browser navigation
        Button(
            onClick = {
                clickCount++
                if (clickCount == 10) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
                    context.startActivity(intent)
                    clickCount = 0
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Click Me ($clickCount)")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Button to fetch data from a URL
        Button(
            onClick = {
                fetchContentFromUrl { result ->
                    fetchedText = result
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Green,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Fetch Data")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Button to fetch data from a socket connection
        Button(
            onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    fetchedText = connectToSocket()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Magenta,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Fetch Socket Data")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Display fetched content
        Text(
            text = fetchedText,
            modifier = Modifier.padding(16.dp)
        )
    }
}

fun fetchContentFromUrl(onResult: (String) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val url = URL("https://www.android.com/")
            val connection = url.openConnection() as HttpURLConnection
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val result = reader.use { it.readText() }
            withContext(Dispatchers.Main) {
                onResult(result)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                onResult("Failed to fetch data")
            }
        }
    }
}

suspend fun connectToSocket(): String {
    val host = "www.android.com"
    val port = 80

    return withContext(Dispatchers.IO) {
        var socket: Socket? = null
        try {
            socket = Socket(host, port)
            val outputStream = PrintWriter(socket.getOutputStream(), true)
            outputStream.println("GET / HTTP/1.1")
            outputStream.println("Host: $host")
            outputStream.println("Connection: close")
            outputStream.println()

            val inputStream = BufferedReader(InputStreamReader(socket.getInputStream()))
            val response = StringBuilder()
            var line: String?

            while (inputStream.readLine().also { line = it } != null) {
                response.append(line).append("\n")
            }

            response.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            "Failed to connect to socket"
        } finally {
            socket?.close()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HackingAndroidTheme {
        MyButtons()
    }
}
