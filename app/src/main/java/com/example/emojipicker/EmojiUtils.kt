package com.example.emojipicker

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun loadEmojis(context: Context): List<EmojiCategory> {
    val inputStream = context.assets.open("Emojies.json")
    val json = inputStream.bufferedReader().use { it.readText() }
    val type = object : TypeToken<List<EmojiCategory>>() {}.type
    return Gson().fromJson(json, type)
}
