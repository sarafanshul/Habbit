package com.projectdelta.habbit.data.model.entities

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.projectdelta.habbit.util.system.lang.Converters
import java.io.Serializable

@Keep
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

):Serializable{
	override fun equals(other: Any?): Boolean {
		if( javaClass != other?.javaClass ) return false

		other as Task
		if( id != other.id ) return false

		return true
	}
}