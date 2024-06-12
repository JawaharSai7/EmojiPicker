package com.example.emojipicker

data class Emoji(
    val emojiSymbol: String,
    val keyword: String
)

data class EmojiCategory(
    val title: String,
    val items: List<Emoji>
)
