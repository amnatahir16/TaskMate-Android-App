package com.example.app

/** One row of data in the list. */
data class Task(
    val id: Int,
    val title: String,
    var isDone: Boolean = false
)
