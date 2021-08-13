package com.projectdelta.habbit.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.projectdelta.habbit.data.model.entities.Day
import com.projectdelta.habbit.data.model.entities.Task
import java.io.Serializable

@Keep
data class FirestoreQueryObject (

    @SerializedName("Day")
    var Day : List<Day>? = null ,

    @SerializedName("Task")
    var Task : List<Task>? = null ,

    @SerializedName("uid")
    var uid : String? = null ,

):Serializable
