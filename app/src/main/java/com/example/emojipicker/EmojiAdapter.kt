package com.example.emojipicker

import com.example.emojipicker.Emoji
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmojiAdapter(
    private var emojis: List<Emoji>,
    private val listener: (Emoji) -> Unit,
    private val recentEmojis: MutableList<Emoji> // Add recentEmojis list here
) : RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder>() {

    inner class EmojiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emojiText: TextView = itemView.findViewById(R.id.tv_emoji)

        fun bind(emoji: Emoji) {
            emojiText.text = emoji.emojiSymbol
            itemView.setOnClickListener {
                listener(emoji)
                addToRecents(emoji)
            }
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

    private fun addToRecents(emoji: Emoji) {
        // Add emoji to recentEmojis if it's not already present
        if (!recentEmojis.contains(emoji)) {
            if (recentEmojis.size >= 20) {
                recentEmojis.removeAt(0) // Remove the oldest emoji if list exceeds capacity
            }
            recentEmojis.add(emoji)
        } else {
            // If emoji is already in recentEmojis, move it to the end (most recent position)
            recentEmojis.remove(emoji)
            recentEmojis.add(emoji)
        }
    }

    fun updateList(newList: List<Emoji>) {
        emojis = newList
        notifyDataSetChanged()
    }
}
