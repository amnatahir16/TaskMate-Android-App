package com.example.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * Saves and loads tasks using SharedPreferences so the list
 * survives closing and reopening the app. Tasks are stored as a JSON string.
 */
object TaskStorage {

    private const val PREFS_NAME = "my_tasks_prefs"
    private const val KEY_TASKS = "tasks"

    fun save(context: Context, tasks: List<Task>) {
        val array = JSONArray()
        for (task in tasks) {
            val obj = JSONObject()
            obj.put("id", task.id)
            obj.put("title", task.title)
            obj.put("isDone", task.isDone)
            array.put(obj)
        }
        prefs(context).edit().putString(KEY_TASKS, array.toString()).apply()
    }

    fun load(context: Context): MutableList<Task> {
        val json = prefs(context).getString(KEY_TASKS, null) ?: return defaultTasks()
        val tasks = mutableListOf<Task>()
        val array = JSONArray(json)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            tasks.add(
                Task(
                    id = obj.getInt("id"),
                    title = obj.getString("title"),
                    isDone = obj.getBoolean("isDone")
                )
            )
        }
        return tasks
    }

    /** Sample tasks shown the very first time the app is opened. */
    private fun defaultTasks() = mutableListOf(
        Task(1, "Buy groceries"),
        Task(2, "Finish Android assignment"),
        Task(3, "Call the dentist")
    )

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
