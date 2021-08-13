package com.projectdelta.habbit.util.database

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.projectdelta.habbit.data.model.FirestoreQueryObject
import com.projectdelta.habbit.data.repository.TasksRepositoryImpl
import com.projectdelta.habbit.util.database.firebase.FirebaseUtil.Companion.USERS
import com.projectdelta.habbit.util.database.firebase.FirebaseUtil.Companion.getAuth
import com.projectdelta.habbit.util.database.firebase.FirebaseUtil.Companion.getDocumentUser
import com.projectdelta.habbit.util.lang.darkToast
import com.projectdelta.habbit.util.lang.isOnline
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

    private var updateJob: Job? = null
    private lateinit var ioScope: CoroutineScope



    /**
     * my garbage collector
     */
    private fun destroyEverything() {
        updateJob?.cancel()
        if( this::ioScope.isInitialized )
            ioScope.cancel()
    }

    fun syncNow( activity : Activity ){

        if( getAuth().currentUser == null ){
            activity.darkToast("Please sign in to use this feature")
        }
        else if( ! activity.isOnline() ){
            activity.darkToast("No network connection available")
        }
        else {
            MaterialAlertDialogBuilder(activity).apply {
                setTitle("Are you saving data to cloud or restoring from cloud ?")
                setPositiveButton("Save") { _, _ ->
                    syncFromLocalDB(activity)
                }
                setNeutralButton("Restore") { _, _ ->
                    syncFromCloud(activity.baseContext)
                }
                create()
            }.show()
        }
    }

    private fun syncFromLocalDB(activity: Activity) {
        val auth = getAuth()
        if( auth.currentUser != null ) {
            val user = auth.currentUser!!
            val doc = getDocumentUser()
            doc.document( user.uid ).get()
                .addOnSuccessListener { document ->
                    if( document == null ){
                        // new document addition
                        addNewDocument(activity.baseContext ,doc , user.uid)
                    }
                    else {
                        // document  , ask then add .
                        MaterialAlertDialogBuilder(activity).apply {
                            setTitle( "A save with same name was found!" )
                            setMessage( "Saved data for ${user.displayName} already exists , do you want to rewrite saved data ?" )
                            setPositiveButton("Rewrite"){_ , _ ->
                                addNewDocument(activity.baseContext , doc , user.uid)
                            }
                            setNeutralButton("CANCEL"){_ , _ -> }
                            create()
                        }.show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error getting document", e)
                }
        }
    }

    /**
     * Warning instance of context is used inside a co-routine please fucking take care of memory leak
     */
    private fun addNewDocument( mContext : Context , doc: CollectionReference , id : String ){
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "addNewDocument: handler", exception)
            destroyEverything()
        }
        updateJob = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch(handler) {
            val taskData = async { repositoryImpl.getAllTasksOffline() }
            val dayData = async { repositoryImpl.getAllDayOffline() }
            addNewDocument( mContext , doc , id , FirestoreQueryObject( dayData.await() , taskData.await() , id ) )
        }
    }

    /**
     * Warning instance of context is used inside a co-routine please fucking take care of memory leak
     */
    private suspend fun addNewDocument( mContext: Context , doc: CollectionReference , id : String , firestoreQueryObject: FirestoreQueryObject) {
        doc
            .document( id )
            .set(firestoreQueryObject)
            .addOnSuccessListener { _ ->
                Log.d(TAG, "DocumentSnapshot added with ID: $id")
                mContext.darkToast("Sync completed successfully")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                mContext.darkToast("Sync failed!, Retry again later")
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
                        addDataToLocalDB( document.toObject( FirestoreQueryObject::class.java ) )
                        Log.d(TAG, "Found document for ${auth.currentUser!!.uid}")
                        context.darkToast("Sync successful")
                    }else {
                        Log.d(TAG, "No such document for ${auth.currentUser!!.uid}")
                        context.darkToast("No saved data found!")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "get() failed with", e)
                    context.darkToast("Restoration failed!")
                }
        }
    }

    private fun addDataToLocalDB(response: FirestoreQueryObject?) {
        Log.i(TAG, "addDataToLocalDB: $response")
        if( response != null && response.Task != null ) {
            val handler = CoroutineExceptionHandler { _, exception ->
                Log.e(TAG, "addDataToLocalDB: handler", exception)
                destroyEverything()
            }
            updateJob = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch(handler) {

                response.Task?.map { T ->  async {
                    repositoryImpl.insertTask(T)
                } }?.forEach { it.await() }

                response.Day?.map { D ->  async {
                    repositoryImpl.insertDay( D )
                } }?.forEach { it.await() }
                Log.d(TAG,"addDataToLocalDB: adding data of size ${response?.Day?.size} , ${response?.Task?.size}")
            }
        }
    }

    /**
     * Testing functions to move to Test later
     */
    private fun getTestDataLocalAndCompare( compareTo : FirestoreQueryObject){
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

    private fun testData(from : FirestoreQueryObject, to : FirestoreQueryObject){
        if(from != to){
            Log.e(TAG, "testData: $from")
            Log.e(TAG, "testData: $to")
        }else {
            Log.d(TAG, "testData: Pass")
        }
    }

}