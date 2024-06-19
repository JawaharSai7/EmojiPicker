package com.example.emojipicker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmojiAdapter(private var emojis: List<Emoji>, private val listener: (Emoji) -> Unit) :
    RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder>() {
    inner class EmojiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emojiText: TextView = itemView.findViewById(R.id.tv_emoji)

        fun bind(emoji: Emoji) {
            emojiText.text = emoji.emojiSymbol
            itemView.setOnClickListener { listener(emoji) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_emoji, parent, false)
        return EmojiViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        holder.bind(emojis[position])
    }

    override fun getItemCount(): Int = emojis.size

    fun updateList(newList: List<Emoji>) {
        emojis = newList
        notifyDataSetChanged()
    }

}