package com.projectdelta.habbit.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.projectdelta.habbit.util.lang.Converters
import java.io.Serializable

@Entity
data class Task(

	@PrimaryKey(autoGenerate = false)
	@SerializedName("id")
	var id : Long = 0,

	@SerializedName("taskName")
	var taskName : String = "",

	@TypeConverters( Converters::class )
	@SerializedName("lastDayCompleted")
	var lastDayCompleted : MutableList<Long> = mutableListOf(),

	@SerializedName("importance")
	var importance : Float = 0F ,

	@field:JvmField // use this annotation if your Boolean field is prefixed with 'is'
	@SerializedName("isNotificationEnabled")
	var isNotificationEnabled : Boolean = true,

	@SerializedName("summary")
	var summary : String = "",

	@SerializedName("skipTill")
	var skipTill : Long = -1 ,

	@field:JvmField // use this annotation if your Boolean field is prefixed with 'is'
	@SerializedName("isSkipAfterEnabled")
	var isSkipAfterEnabled : Boolean = false ,

	@SerializedName("skipAfter")
	var skipAfter : Long = -1 ,

):Serializable