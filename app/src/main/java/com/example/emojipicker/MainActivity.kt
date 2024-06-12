package com.example.emojipicker

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val emojis = loadEmojis(this)

        val button: Button = findViewById(R.id.btn_open_emoji_picker)
        button.setOnClickListener {
            EmojiPickerDialog(this, emojis.flatMap { it.items }) { emoji ->
                // Handle emoji selection
                Toast.makeText(this, "Selected: ${emoji.emojiSymbol}", Toast.LENGTH_SHORT).show()
            }.show()
        }
    }
}
