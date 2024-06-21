package com.example.emojipicker

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load emojis from JSON and convert to a mutable list
        val emojiCategories = loadEmojis(this).toMutableList()

        // Add "Recents" category before all other categories
        val recentsCategory = EmojiCategory("Recents", EmojiManager.getRecents())
        emojiCategories.add(0, recentsCategory)

        val button: Button = findViewById(R.id.btn_open_emoji_picker)
        button.setOnClickListener {
            EmojiPickerDialog(this, emojiCategories) { emoji ->
                EmojiManager.addRecent(emoji) // Add emoji to recents
                Toast.makeText(this, "Selected: ${emoji.emojiSymbol}", Toast.LENGTH_SHORT).show()
            }.show()
        }
    }
}
