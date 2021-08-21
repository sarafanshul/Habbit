package com.projectdelta.habbit.di

import android.app.Application
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.projectdelta.habbit.R
import com.projectdelta.habbit.util.database.firebase.FirebaseUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

	@Singleton
	@Provides
	fun provideGoogleSignInOption(application: Application): GoogleSignInOptions {
		return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).apply {
			requestIdToken(application.getString(R.string.default_web_client_id))
			requestEmail()
		}.build()
	}

	@Singleton
	@Provides
	fun provideFirebaseUtil(): FirebaseUtil {
		return FirebaseUtil()
	}

}