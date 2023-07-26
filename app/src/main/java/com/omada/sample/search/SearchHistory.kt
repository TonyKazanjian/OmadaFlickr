package com.omada.sample.search

/**
 * A simple cache to show search query history, as a way to make use of the Material 3 SearchBar.
 * Limited to 20 items.
 */
object SearchHistory {

    private val cache = mutableListOf<String>()

    @Synchronized
    fun add(item: String) {
        if (item.isNotEmpty() && !cache.contains(item)) {
            if (cache.size >= 20) {
                cache.removeLast()
            }
            cache.add(0, item)
        } else if (cache.contains(item)) {
            cache.remove(item)
            cache.add(0, item)
        }
    }

    @Synchronized
    fun getHistory(): List<String> {
        return cache.toList()
    }
}