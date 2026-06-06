package com.example.app

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Loaded from storage in onCreate so tasks survive app restarts.
    private lateinit var tasks: MutableList<Task>
    private var nextId = 1

    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load saved tasks (or the sample list on first launch).
        tasks = TaskStorage.load(this)
        nextId = (tasks.maxOfOrNull { it.id } ?: 0) + 1

        // Create the adapter, wiring all the row callbacks.
        adapter = TaskAdapter(
            tasks,
            onToggleDone = { position ->
                tasks[position].isDone = !tasks[position].isDone
                adapter.notifyItemChanged(position)
                save()
                updateSubtitle()
            },
            onDelete = { position ->
                tasks.removeAt(position)
                adapter.notifyItemRemoved(position)
                save()
                refreshUi()
            },
            onEdit = { position -> showEditTaskDialog(position) }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.addButton.setOnClickListener { showAddTaskDialog() }

        refreshUi()
    }

    private fun showAddTaskDialog() {
        showTextDialog(
            titleRes = R.string.add_task,
            initialText = "",
            positiveLabel = "Add"
        ) { text ->
            tasks.add(Task(nextId++, text))
            adapter.notifyItemInserted(tasks.size - 1)
            binding.recyclerView.scrollToPosition(tasks.size - 1)
            save()
            refreshUi()
        }
    }

    private fun showEditTaskDialog(position: Int) {
        showTextDialog(
            titleRes = R.string.edit_task,
            initialText = tasks[position].title,
            positiveLabel = "Save"
        ) { text ->
            tasks[position] = tasks[position].copy(title = text)
            adapter.notifyItemChanged(position)
            save()
        }
    }

    /** Reusable text-input dialog used by both Add and Edit. */
    private fun showTextDialog(
        titleRes: Int,
        initialText: String,
        positiveLabel: String,
        onConfirm: (String) -> Unit
    ) {
        val input = EditText(this).apply {
            hint = getString(R.string.new_task_hint)
            setText(initialText)
            setSelection(initialText.length)
            setPadding(48, 32, 48, 32)
        }
        AlertDialog.Builder(this)
            .setTitle(titleRes)
            .setView(input)
            .setPositiveButton(positiveLabel) { _, _ ->
                val text = input.text.toString().trim()
                if (text.isNotEmpty()) onConfirm(text)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun save() = TaskStorage.save(this, tasks)

    /** Show the empty-state view when there are no tasks, and refresh the count. */
    private fun refreshUi() {
        binding.emptyState.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
        binding.recyclerView.visibility = if (tasks.isEmpty()) View.GONE else View.VISIBLE
        updateSubtitle()
    }

    private fun updateSubtitle() {
        val done = tasks.count { it.isDone }
        binding.toolbar.subtitle = "$done of ${tasks.size} done"
    }
}
