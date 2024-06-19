package com.example.emojipicker

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout

class EmojiPickerDialog(context: Context, private val emojiCategories: List<EmojiCategory>, private val listener: (Emoji) -> Unit) :
    Dialog(context) {

    private lateinit var adapter: EmojiAdapter
    private lateinit var allEmojis: List<Emoji>
    private lateinit var recyclerView: RecyclerView
    private lateinit var tabLayout: TabLayout
    private val emojiPositionMap = mutableMapOf<Int, String>() // Mapping positions to categories
    private var isUserScrolling = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_emoji_picker)

        recyclerView = findViewById(R.id.rv_emojis)
        val searchEditText: EditText = findViewById(R.id.et_search)
        tabLayout = findViewById(R.id.tabs_categories)

        allEmojis = emojiCategories.flatMap { it.items }

        adapter = EmojiAdapter(allEmojis, listener)
        recyclerView.layoutManager = GridLayoutManager(context, 5)
        recyclerView.adapter = adapter

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        setupTabs(tabLayout)
        setupScrollListener()
    }

    private val tabLayoutListeners = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            tab?.let {
                if (!isUserScrolling) {
                    smoothScrollToCategory(it.position)
                }
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}
        override fun onTabReselected(tab: TabLayout.Tab?) {}
    }

    private fun setupTabs(tabLayout: TabLayout) {
        tabLayout.addTab(tabLayout.newTab().setText("All"))
        emojiCategories.forEachIndexed { index, category ->
            tabLayout.addTab(tabLayout.newTab().setText(category.title))
            val position = allEmojis.indexOfFirst { it == category.items.first() }
            if (position != -1) {
                emojiPositionMap[position] = category.title
            }
        }

        tabLayout.addOnTabSelectedListener(tabLayoutListeners)
    }

    private fun setupScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                isUserScrolling = newState != RecyclerView.SCROLL_STATE_IDLE
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isUserScrolling) return
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                updateTabSelection(firstVisiblePosition)
            }
        })
    }

    private fun updateTabSelection(position: Int) {
        val category = emojiPositionMap.entries.lastOrNull { it.key <= position }?.value ?: "All"
        for (i in 0 until tabLayout.tabCount) {
            if (tabLayout.getTabAt(i)?.text == category) {
                tabLayout.removeOnTabSelectedListener(tabLayoutListeners)
                tabLayout.selectTab(tabLayout.getTabAt(i))
                tabLayout.addOnTabSelectedListener(tabLayoutListeners)
                break
            }
        }
    }

    private fun filter(text: String) {
        val filteredList = allEmojis.filter { it.keyword.contains(text, true) }
        adapter.updateList(filteredList)
    }

    private fun smoothScrollToCategory(position: Int) {
        val targetPosition = if (position == 0) {
            0
        } else {
            allEmojis.indexOfFirst { it == emojiCategories[position - 1].items.first() }
        }
        if (targetPosition != -1) {
            recyclerView.post {
                (recyclerView.layoutManager as GridLayoutManager).scrollToPositionWithOffset(targetPosition, 0)
            }
        }
    }
}
