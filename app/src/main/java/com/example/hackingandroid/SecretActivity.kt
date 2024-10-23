package com.example.hackingandroid

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SecretActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secret)

        // Find the TextView by its ID
        val debugText = findViewById<TextView>(R.id.textView2)

        // Get the intent that started this activity
        val intent = intent
        val action = intent.action
        val type = intent.type

        if (Intent.ACTION_SEND == action && type != null) {
            if ("text/plain" == type) {
                handleSendText(intent, debugText) // Handle text being sent
            }
        }
    }

    // Function to handle receiving text
    private fun handleSendText(intent: Intent, debugText: TextView) {
        val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (sharedText != null) {
            // Update the TextView with the shared text
            debugText.text = "Shared: $sharedText"
        }
    }
}