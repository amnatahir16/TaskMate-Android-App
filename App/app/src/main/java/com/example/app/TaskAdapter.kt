package com.example.app

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.app.databinding.ItemTaskBinding

/**
 * Connects the list of [Task] objects to the RecyclerView rows.
 *
 * @param tasks       the data to show.
 * @param onToggleDone called when the checkbox is tapped (mark complete / incomplete).
 * @param onDelete     called when the delete button is tapped.
 * @param onEdit       called when the task title is tapped (rename it).
 */
class TaskAdapter(
    private val tasks: List<Task>,
    private val onToggleDone: (position: Int) -> Unit,
    private val onDelete: (position: Int) -> Unit,
    private val onEdit: (position: Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    /** Holds the views for a single row so they aren't looked up repeatedly. */
    inner class TaskViewHolder(val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.doneCheckBox.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onToggleDone(pos)
            }
            binding.deleteButton.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onDelete(pos)
            }
            binding.titleText.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onEdit(pos)
            }
        }
    }

    // 1. Creates a new empty row by inflating item_task.xml.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TaskViewHolder(binding)
    }

    // 2. Puts the data for a given position into the row's views.
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.binding.titleText.text = task.title
        holder.binding.doneCheckBox.isChecked = task.isDone

        // Strike through and fade the text when the task is done.
        holder.binding.titleText.paintFlags = if (task.isDone) {
            holder.binding.titleText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.binding.titleText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
        holder.binding.titleText.alpha = if (task.isDone) 0.5f else 1f
    }

    // 3. Tells the RecyclerView how many rows there are.
    override fun getItemCount(): Int = tasks.size
}
