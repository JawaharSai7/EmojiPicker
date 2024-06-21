package com.example.emojipicker

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout

class EmojiPickerDialog(
    context: Context,
    private var emojiCategories: MutableList<EmojiCategory>,
    private val listener: (Emoji) -> Unit
) : Dialog(context) {
    private lateinit var adapter: EmojiAdapter
    private lateinit var recentAdapter: EmojiAdapter
    private lateinit var allItems: List<Any>
    private lateinit var recyclerView: RecyclerView
    private lateinit var recentRecyclerView: RecyclerView
    private lateinit var tabLayout: TabLayout
    private val categoryPositions = mutableMapOf<String, Int>()
    private var isUserScrolling = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_emoji_picker)

        // Set the dialog to use full width
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        recyclerView = findViewById(R.id.rv_emojis)
        recentRecyclerView = findViewById(R.id.rv_recent_emojis)
        val searchEditText: EditText = findViewById(R.id.et_search)
        tabLayout = findViewById(R.id.tabs_categories)

        allItems = prepareItemList(emojiCategories.filter { it.title != "Recents" })
        adapter = EmojiAdapter(allItems) { emoji ->
            listener(emoji)
            EmojiManager.addRecent(emoji) // Update recent emojis
            updateRecents() // Update the recent emojis in the UI
        }

        val spanCount = 5
        val layoutManager = GridLayoutManager(context, spanCount)
        layoutManager.spanSizeLookup = CustomSpanSizeLookup(allItems, spanCount)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        // Setup Recent Emojis RecyclerView
        recentAdapter = EmojiAdapter(EmojiManager.getRecents()) { emoji ->
            listener(emoji)
            EmojiManager.addRecent(emoji) // Ensure the emoji remains in the recent list
            updateRecents() // Refresh recent emojis
        }

        recentRecyclerView.layoutManager = GridLayoutManager(context, spanCount)
        recentRecyclerView.adapter = recentAdapter

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
            categoryPositions[category.title] = list.size - 1
            list.addAll(category.items)
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
        emojiCategories.filter { it.title != "Recents" }.forEachIndexed { index, category ->
            val tab = tabLayout.newTab()
            val customView = LayoutInflater.from(tabLayout.context).inflate(R.layout.custom_tab_layout, null)
            customView.findViewById<TextView>(R.id.tv_tab_title)?.text = category.title
            tab.customView = customView
            tabLayout.addTab(tab)
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
        val categoryIndex = categoryPositions.entries.lastOrNull { it.value <= position }?.key
        val tabIndex = emojiCategories.indexOfFirst { it.title == categoryIndex }
        if (tabIndex != -1 && tabLayout.selectedTabPosition != tabIndex) {
            tabLayout.removeOnTabSelectedListener(tabLayoutListeners)
            tabLayout.selectTab(tabLayout.getTabAt(tabIndex))
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
        val category = emojiCategories.filter { it.title != "Recents" }[position]
        val targetPosition = categoryPositions[category.title] ?: 0
        recyclerView.post {
            (recyclerView.layoutManager as GridLayoutManager).scrollToPositionWithOffset(targetPosition, 0)
        }
    }

    private fun updateRecents() {
        val recents = EmojiManager.getRecents()
        recentAdapter.updateList(recents)
    }
}
