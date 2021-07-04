package com.projectdelta.habbit.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.projectdelta.habbit.util.lang.Converters
import java.io.Serializable

/**
 * Database table for logging tasks completed day-wise,
 * @param id is day-number from epoch, hence unique
 * @param tasks is list of [Task.id]
 */
@Entity
data class Day(

	@PrimaryKey(autoGenerate = false)
	val id: Long,

	@TypeConverters( Converters::class )
	val tasks: MutableList<Long>,
):Serializable
