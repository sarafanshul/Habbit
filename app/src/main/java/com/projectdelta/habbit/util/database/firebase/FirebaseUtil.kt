package com.projectdelta.habbit.util.database.firebase

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.projectdelta.habbit.R

class FirebaseUtil (context: Context) {

    companion object{

        const val USERS = "users"
        val SOURCE = Source.DEFAULT

        fun getDocumentUser() : CollectionReference {
            return Firebase.firestore.collection(USERS)
        }

        fun getAuth(): FirebaseAuth {
            return Firebase.auth
        }

    }


    val gso by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    val googleSignInClient by lazy {
        GoogleSignIn.getClient(context, gso)
    }

}