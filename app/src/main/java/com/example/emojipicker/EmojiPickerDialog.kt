package com.example.emojipicker

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout

class EmojiPickerDialog(
    context: Context,
    private val emojiCategories: List<EmojiCategory>,
    private val listener: (Emoji) -> Unit
) : Dialog(context) {
    private lateinit var adapter: EmojiAdapter
    private lateinit var allItems: List<Any> // List with headers and emojis
    private lateinit var recyclerView: RecyclerView
    private lateinit var tabLayout: TabLayout
    private val emojiPositionMap = mutableMapOf<Int, Int>() // Mapping positions to tab index
    private var isUserScrolling = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_emoji_picker)

        recyclerView = findViewById(R.id.rv_emojis)
        val searchEditText: EditText = findViewById(R.id.et_search)
        tabLayout = findViewById(R.id.tabs_categories)

        allItems = prepareItemList(emojiCategories)

        adapter = EmojiAdapter(allItems, listener)

        val spanCount = 5 // Number of columns for emojis
        val layoutManager = GridLayoutManager(context, spanCount)
        layoutManager.spanSizeLookup = CustomSpanSizeLookup(allItems, spanCount)

        recyclerView.layoutManager = layoutManager
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

    private fun prepareItemList(emojiCategories: List<EmojiCategory>): List<Any> {
        val list = mutableListOf<Any>()
        emojiCategories.forEach { category ->
            list.add(category.title) // Add header
            list.addAll(category.items) // Add emojis under this category
        }
        return list
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
        emojiCategories.forEachIndexed { index, category ->
            val tab = tabLayout.newTab()
            val customView = LayoutInflater.from(tabLayout.context).inflate(R.layout.custom_tab_layout, null)
            customView.findViewById<TextView>(R.id.tv_tab_title)?.text = category.title
            tab.customView = customView
            tabLayout.addTab(tab)

            val position = allItems.indexOfFirst { it == category.title }
            if (position != -1) {
                emojiPositionMap[position] = index
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
        val categoryIndex = emojiPositionMap.entries.lastOrNull { it.key <= position }?.value ?: 0
        if (tabLayout.selectedTabPosition != categoryIndex) {
            tabLayout.removeOnTabSelectedListener(tabLayoutListeners)
            tabLayout.selectTab(tabLayout.getTabAt(categoryIndex))
            tabLayout.addOnTabSelectedListener(tabLayoutListeners)
        }
    }

    private fun filter(text: String) {
        val filteredList = allItems.filter {
            it is Emoji && it.keyword.contains(text, true)
        }
        adapter.updateList(filteredList)
    }

    private fun smoothScrollToCategory(position: Int) {
        val targetPosition = if (position == 0) {
            0
        } else {
            allItems.indexOfFirst { it == emojiCategories[position].title }
        }
        if (targetPosition != -1) {
            recyclerView.post {
                (recyclerView.layoutManager as GridLayoutManager).scrollToPositionWithOffset(
                    targetPosition,
                    0
                )
            }
        }
    }
}
