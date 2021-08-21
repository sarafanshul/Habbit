package com.projectdelta.habbit.data.model.entities

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import com.projectdelta.habbit.util.system.lang.Converters
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
@Keep
@Entity
data class Day(

	@PrimaryKey(autoGenerate = false)
	@SerializedName("id")
	var id: Long = 0,

	@TypeConverters(Converters::class)
	@SerializedName("tasksID")
	var tasksID: MutableList<Long> = mutableListOf(),

	@TypeConverters(Converters::class)
	@SerializedName("tasksTitle")
	var tasksTitle: MutableList<String> = mutableListOf(),

	) : Serializable {

	override fun equals(other: Any?): Boolean {
		if (javaClass != other?.javaClass) return false

		other as Day
		if (id != other.id) return false

		return true
	}
}
