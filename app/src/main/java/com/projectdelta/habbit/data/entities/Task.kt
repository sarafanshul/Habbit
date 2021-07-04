package com.projectdelta.habbit.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.projectdelta.habbit.util.lang.Converters
import java.io.Serializable

@Entity
data class Task(

	@PrimaryKey(autoGenerate = false)
	val id : Long,

	var taskName : String,

	@TypeConverters( Converters::class )
	var lastDayCompleted : MutableList<Long> = mutableListOf(),
	var importance : Float ,

	var isNotificationEnabled : Boolean = true,
	var summary : String = "",

	var skipTill : Long = -1 ,

	var isSkipAfterEnabled : Boolean = false ,
	var skipAfter : Long = -1 ,

):Serializable