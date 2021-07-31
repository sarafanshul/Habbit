package com.projectdelta.habbit.util.database

import android.content.Context
import android.util.Log
import com.projectdelta.habbit.data.TasksDatabase
import com.projectdelta.habbit.util.database.firebase.FirebaseUtil.Companion.getAuth
import com.projectdelta.habbit.util.database.firebase.FirebaseUtil.Companion.getDocumentUser
import com.projectdelta.habbit.util.lang.darkToast
import kotlinx.coroutines.*

class DatabaseUtil () {

    companion object{
        private const val TAG = "DatabaseUtil"
    }

    private var updateJob: Job? = null

    fun nukeLocal( context: Context ){
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "deleteAllData: handler", exception)
            destroyEverything()
        }
        updateJob = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch(handler){
            TasksDatabase.getInstance(context).clearAllTables()
        }
        context.darkToast( "Success!" )
    }

    fun nukeCloud( context: Context ){
        val user = getAuth().currentUser
        val doc = getDocumentUser()
        if( user != null ){
            doc.document(user.uid)
                .delete()
                .addOnSuccessListener {
                    context.darkToast( "Success!" )
                    Log.d(TAG, "DocumentSnapshot successfully deleted!")
                }
                .addOnFailureListener { e ->
                    context.darkToast( "Some error occurred." )
                    Log.w(TAG, "Error deleting document", e)
                }
        }
    }

    private fun destroyEverything() {

    }
}