package com.example.emojipicker

import androidx.recyclerview.widget.DiffUtil

class EmojiDiffCallback(
    private val oldList: List<Any>,
    private val newList: List<Any>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return if (oldItem is Emoji && newItem is Emoji) {
            oldItem.emojiSymbol == newItem.emojiSymbol
        } else if (oldItem is String && newItem is String) {
            oldItem == newItem
        } else {
            false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return if (oldItem is Emoji && newItem is Emoji) {
            oldItem == newItem
        } else if (oldItem is String && newItem is String) {
            oldItem == newItem
        } else {
            false
        }
    }
}
