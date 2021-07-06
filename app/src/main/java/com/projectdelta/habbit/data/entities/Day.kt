package com.projectdelta.habbit.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.projectdelta.habbit.util.lang.Converters
import java.io.Serializable

/**
 * Database table for logging tasks completed day-wise,
 * @param id is day-number from epoch, hence unique
 * @param tasksID is list of [Task.id] for querying DB for more info
 * @param tasksTitle is list of [Task.taskName] for showing in titles
 *
 * Both ID and Title are used for persistence and reduce load on DB , ie:when we want to see tasks done
 * on a day we can use [tasksTitle] instead of querying DB for ID's
 */
@Entity
data class Day(

	@PrimaryKey(autoGenerate = false)
	val id: Long,

	@TypeConverters( Converters::class )
	val tasksID: MutableList<Long>,

	@TypeConverters( Converters::class )
	val tasksTitle : MutableList<String>,

):Serializable