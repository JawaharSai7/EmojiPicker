package com.example.emojipicker

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
//just checking with git commits
class EmojiPickerDialog(context: Context, private val emojis: List<Emoji>, private val listener: (Emoji) -> Unit) :
    Dialog(context) {

    private lateinit var adapter: EmojiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_emoji_picker)

        val recyclerView: RecyclerView = findViewById(R.id.rv_emojis)
        val searchEditText: EditText = findViewById(R.id.et_search)

        adapter = EmojiAdapter(emojis, listener)
        recyclerView.layoutManager = GridLayoutManager(context, 5)
        recyclerView.adapter = adapter

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun filter(text: String) {
        val filteredList = emojis.filter { it.keyword.contains(text, true) }
        adapter.updateList(filteredList)
    }
}
