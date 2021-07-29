package com.projectdelta.habbit.util.firebase

import android.app.Activity
import android.content.Context
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.Source
import com.projectdelta.habbit.R
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class FirebaseUtil @Inject constructor(
    @ActivityContext context: Context
) {

    companion object{
        const val USERS = "users"
        val SOURCE = Source.DEFAULT
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