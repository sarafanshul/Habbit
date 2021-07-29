package com.projectdelta.habbit.data

import com.google.gson.annotations.SerializedName
import com.projectdelta.habbit.data.entities.Day
import com.projectdelta.habbit.data.entities.Task
import java.io.Serializable

data class FirestoreQueryObject (

    @SerializedName("Day")
    var Day : List<Day>? = null ,

    @SerializedName("Task")
    var Task : List<Task>? = null ,

    @SerializedName("uid")
    var uid : String? = null ,

):Serializable
