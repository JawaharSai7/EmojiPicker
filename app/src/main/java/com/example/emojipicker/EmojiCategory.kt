package com.example.emojipicker

data class EmojiCategory(
    val title: String,
    val items: List<Emoji>
)

object EmojiManager {
    private val recentEmojis = mutableListOf<Emoji>()

    fun addRecent(emoji: Emoji) {
        // Remove emoji if it already exists to avoid duplication
        recentEmojis.remove(emoji)
        recentEmojis.add(0, emoji)
        if (recentEmojis.size > 10) { // Limit to 20 recent emojis
            recentEmojis.removeAt(recentEmojis.size - 1)
        }
    }

    fun getRecents(): List<Emoji> {
        return recentEmojis.toList() // Return a copy to avoid modification
    }
}
