package com.projectdelta.habbit.util

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.projectdelta.habbit.data.FirestoreQueryObject
import com.projectdelta.habbit.data.entities.Task
import com.projectdelta.habbit.repository.TasksRepositoryImpl
import com.projectdelta.habbit.util.firebase.FirebaseUtil.Companion.SOURCE
import com.projectdelta.habbit.util.firebase.FirebaseUtil.Companion.USERS
import com.projectdelta.habbit.util.lang.toast
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncUtil @Inject constructor(
   private val repositoryImpl: TasksRepositoryImpl
) {
    companion object{
        private const val TAG = "SyncUtil"
    }

    fun syncNow( activity : Activity ){
        MaterialAlertDialogBuilder(activity).apply {
            setTitle( "Are you saving data to cloud or restoring from cloud ?" )
            setPositiveButton("Save"){ _ , _ ->
                syncFromLocalDB(activity.baseContext)
            }
            setNeutralButton("Restore"){ _ , _ ->
                syncFromCloud(activity.baseContext)
            }
            create()
        }.show()
    }

    private fun syncFromLocalDB(context: Context) {
        val db = Firebase.firestore
        val auth = Firebase.auth
        if( auth.currentUser != null ) {
            val job = GlobalScope.launch(Dispatchers.IO) {
                val taskData = async { repositoryImpl.getAllTasksOffline() }
                val dayData = async { repositoryImpl.getAllDayOffline() }
                db.collection(USERS)
                    .document(auth.currentUser!!.uid)
                    .set(
                        hashMapOf(
                            "uid" to auth.currentUser!!.uid,
                            "Task" to taskData.await(),
                            "Day" to dayData.await()
                        )
                    )
                    .addOnSuccessListener { _ ->
                        Log.d(TAG, "DocumentSnapshot added with ID: ${auth.currentUser!!.uid}")
                        context.toast("Sync completed successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                        context.toast("Sync failed!")
                    }

            }
        }
    }

    private fun syncFromCloud(context: Context){
        val db = Firebase.firestore
        val auth = Firebase.auth
        if( auth.currentUser != null ) {
            val docRef =  db.collection(USERS).document(auth.currentUser!!.uid)
            docRef
                .get()
                .addOnSuccessListener { document ->
                    if( document != null ){
                        document.toObject( FirestoreQueryObject::class.java )
                        addDataToLocalDB( document.toObject( FirestoreQueryObject::class.java ) )
                    }else {
                        Log.d(TAG, "No such document for ${auth.currentUser!!.uid}")
                        context.toast("No saved data found!")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "get() failed with", e)
                    context.toast("Restoration failed!")
                }
        }
    }

    private fun addDataToLocalDB(response: FirestoreQueryObject?) {
//        getTestDataLocalAndCompare(response!!)
    }


    private fun testData( from : FirestoreQueryObject , to : FirestoreQueryObject ){
        if(from != to){
            Log.e(TAG, "testData: $from")
            Log.e(TAG, "testData: $to")
        }else {
            Log.d(TAG, "testData: Pass")
        }
    }

    private fun getTestDataLocalAndCompare( compareTo : FirestoreQueryObject ){
        val x = FirestoreQueryObject()
        GlobalScope.launch(Dispatchers.IO) {
            val taskData = async { repositoryImpl.getAllTasksOffline() }
            val dayData = async { repositoryImpl.getAllDayOffline() }
            x.Day = dayData.await()
            x.Task = taskData.await()
            x.uid = Firebase.auth.currentUser!!.uid
            testData( x , compareTo )
        }
    }

}