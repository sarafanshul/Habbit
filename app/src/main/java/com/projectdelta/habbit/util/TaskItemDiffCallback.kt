package com.projectdelta.habbit.util

import androidx.recyclerview.widget.DiffUtil
import com.projectdelta.habbit.data.model.entities.Task

class TaskItemDiffCallback(
	var oldTaskList : List<Task>,
	var newTaskList : List<Task>
): DiffUtil.Callback(){

	override fun getOldListSize(): Int {
		return oldTaskList.size
	}

	override fun getNewListSize(): Int {
		return newTaskList.size
	}

	/**
	 * This method will be called to check whether old and new items are the same.
	 */
	override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		return oldTaskList[oldItemPosition].equals( newTaskList[newItemPosition] )
	}

	/**
	 * This method will be called to check whether old and new items represent the same item visually.
	 * This will only be called when [areItemsTheSame] returns true
	 *
	 * The fields that you should check here are the fields that can be visually seen on the list.
	 * If your list displays title and description . Then this method should contain these:
	 *
	 * ```Kotlin
	 * fun areContentsTheSame(oldItem: Item, newItem: Item) {
	 *     return oldItem.title == newItem.title &&
	 *     oldItem.description == newItem.description
	 * }
	 * ```
	 * [See more](https://blog.usejournal.com/demystifying-diffutil-itemcallback-class-8c0201cc69b1)
	 */
	override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
		return oldTaskList[oldItemPosition].taskName == newTaskList[newItemPosition].taskName &&
				oldTaskList[oldItemPosition].summary == newTaskList[newItemPosition].summary &&
				oldTaskList[oldItemPosition].importance == newTaskList[newItemPosition].importance
	}
}