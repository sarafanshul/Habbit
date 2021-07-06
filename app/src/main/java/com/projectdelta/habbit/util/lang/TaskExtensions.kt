package com.projectdelta.habbit.util.lang

import com.projectdelta.habbit.data.entities.Task

/**
 * Returns all unfinished tasks till X -> [0 , X).
 *
 * @param X day from epoch.
 * @return List<Task> if it has permissions.
 */
fun List<Task>.unfinishedTill( X : Long ) = filter {
		( it.skipTill < X && (it.lastDayCompleted.isEmpty() || it.lastDayCompleted.last() < X) )
	}.sortedBy { -it.importance }


/**
 * Returns all skipped tasks from  X -> [X , Inf).
 *
 * @param X day from epoch.
 * @param Y current time in ms (From midnight)
 * @return List<Task> if it has permissions.
 */
fun List<Task>.skippedTill( X : Long , Y : Long ) = filter {
		(it.skipTill >= X || (it.isSkipAfterEnabled && it.skipAfter <= Y) )
	}.sortedBy { -it.importance }


/**
 * Returns all Completed tasks on X
 *
 * @param X day from epoch.
 * @return List<Task> if it has permissions.
 */
fun List<Task>.completedTill( X : Long ) = filter {
		( !it.lastDayCompleted.isNullOrEmpty() && it.skipTill < X && it.lastDayCompleted.last() == X )
	}.sortedBy { -it.importance }


/**
 * Returns all unfinished tasks with notifications enabled till X -> [0 , X).
 *
 * @param X day from epoch.
 * @return List<Task> if it has permissions.
 */
fun List<Task>.unfinishedNotifyTill( X : Long ) = filter {
	( it.skipTill < X && it.isNotificationEnabled && (it.lastDayCompleted.isEmpty() || it.lastDayCompleted.last() < X) )
}

/**
 * Returns List of unfinished Tasks before [Task.skipAfter]
 * @param X current time in ms (From midnight)
 * @return List<Task> unfinished tasks
 */
fun List<Task>.tasksBeforeSkipTime( X : Long ) = filter{
	( !it.isSkipAfterEnabled || it.skipAfter > X )
}

/**
 * Returns Id (Int) of a task
 */
fun Task.hash() = (this.id%((1L shl 31) - 1)).toInt()