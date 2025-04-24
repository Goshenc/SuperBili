package com.example.superbili

sealed class ListItem {
    data class Group(
        val title: String,
        val children: MutableList<Child>,
        var isExpanded: Boolean = false
    ) : ListItem()

    data class Child(
        val videoId: Long,
        val imageId: Int,
        val viewNumber: String,
        val danmuNumber: String,
        val time: String,
        val title: String,
        val upName: String
    ) : ListItem()
}