package com.example.emojipicker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmojiAdapter(
    private var items: List<Any>, // List containing both String headers and Emoji items
    private val listener: (Emoji) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_TYPE_HEADER = 0
    private val ITEM_TYPE_EMOJI = 1

    inner class EmojiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val emojiText: TextView = itemView.findViewById(R.id.tv_emoji)

        fun bind(emoji: Emoji) {
            emojiText.text = emoji.emojiSymbol
            itemView.setOnClickListener {
                listener(emoji)
            }
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerText: TextView = itemView.findViewById(R.id.tv_category_heading)

        fun bind(header: String) {
            headerText.text = header
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is String) ITEM_TYPE_HEADER else ITEM_TYPE_EMOJI
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_emoji_category, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_emoji, parent, false)
            EmojiViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.bind(items[position] as String)
        } else if (holder is EmojiViewHolder) {
            holder.bind(items[position] as Emoji)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newList: List<Any>) {
        items = newList
        notifyDataSetChanged()
    }
}
