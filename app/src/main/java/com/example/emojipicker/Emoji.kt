package com.example.emojipicker
data class Emoji(
    val emojiSymbol: String,
    val keyword: String,
    val timestamp: Long = System.currentTimeMillis() // Timestamp of selection
)