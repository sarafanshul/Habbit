package com.projectdelta.habbit.ui.main.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.projectdelta.habbit.R
import com.projectdelta.habbit.databinding.FragmentMenuBinding
import com.projectdelta.habbit.ui.base.BaseViewBindingFragment
import com.projectdelta.habbit.ui.navigation.NavigationUtil
import com.projectdelta.habbit.util.database.firebase.FirebaseUtil
import com.projectdelta.habbit.util.system.lang.capitalized
import com.projectdelta.habbit.util.system.lang.darkToast
import com.projectdelta.habbit.util.system.lang.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MenuFragment : BaseViewBindingFragment<FragmentMenuBinding>() {

	companion object {
		private const val TAG = "MenuFragment"
	}

	private lateinit var auth: FirebaseAuth

	@Inject
	lateinit var firebaseUtil: FirebaseUtil

	private val startForResultSignIn =
		registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
			if (result.resultCode == Activity.RESULT_OK) {
				val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
				if (task.isSuccessful) {
					// Google Sign In was successful, authenticate with Firebase
					val account = task.getResult(ApiException::class.java)!!
					Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
					requireActivity().toast("Sign In successful!")
					firebaseAuthWithGoogle(account.idToken!!)
				} else {
					Log.d(TAG, "firebaseAuthWithGoogle:" + "Failed")
					// Google Sign In failed, update UI appropriately
					requireActivity().toast("Unable to sign in.")
				}
			}
		}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentMenuBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		auth = Firebase.auth

		setLayout()

	}

	private fun setLayout() {

		setupUser(auth.currentUser)

		binding.menuBtnLogin.setOnClickListener {
			if (auth.currentUser == null) {
				signIn()
			} else {
				MaterialAlertDialogBuilder(requireActivity()).apply {
					setTitle("Sign out of Habbit?")
					setPositiveButton("OK") { _, _ ->
						signOut()
					}
					setNeutralButton("CANCEL") { _, _ -> }
					create()
				}.show()
			}
		}

		binding.menuBtnAbout.setOnClickListener {
			NavigationUtil.about(requireActivity())
		}

		binding.menuBtnSetting.setOnClickListener {
			NavigationUtil.settings(requireActivity())
		}

		binding.menuBtnInsights.setOnClickListener {
			NavigationUtil.insights(requireActivity())
		}

		binding.menuBtnUser.setOnClickListener {
			requireActivity().darkToast("Coming soon...")
		}

		binding.menuBtnUserData.setOnClickListener {
			NavigationUtil.settings(requireActivity())
		}

		binding.menuBtnHelp.setOnClickListener {
			NavigationUtil.helpAndFeedback(requireActivity())
		}

	}

	private fun signIn() {
		val signInIntent = firebaseUtil.googleSignInClient(requireActivity()).signInIntent
		startForResultSignIn.launch(signInIntent)
	}

	private fun signOut() {
		Firebase.auth.signOut()
	}

	private fun firebaseAuthWithGoogle(idToken: String) {
		val credential = GoogleAuthProvider.getCredential(idToken, null)
		auth.signInWithCredential(credential)
			.addOnCompleteListener(requireActivity()) { task ->
				if (task.isSuccessful) {
					// Sign in success, update UI with the signed-in user's information
					Log.d(TAG, "signInWithCredential:success")
					val user = auth.currentUser
					setupUser(user)
				} else {
					// If sign in fails, display a message to the user.
					Log.w(TAG, "signInWithCredential:failure", task.exception)
					setupUser(null)
				}
			}
	}

	@SuppressLint("SetTextI18n")
	private fun setupUser(user: FirebaseUser?) {
		if (user == null) {
			binding.userName.text = "Guest"
			binding.userEmail.text = "Not logged in!"
			binding.userImage.setImageDrawable(
				ContextCompat.getDrawable(
					requireActivity(),
					R.drawable.ic_guest_user
				)
			)
			binding.menuTvLogInOut.text = "Login"
		} else {
			Log.d(TAG, "setupUser: ${user.displayName} @ ${user.email}")
			binding.userName.text =
				user.displayName?.split(" ")?.joinToString(" ") { it.capitalized() }
			binding.userEmail.text = user.email
			Glide.with(this).load(user.photoUrl).into(binding.userImage)
			binding.menuTvLogInOut.text = "Logout"
		}
	}

	override fun onResume() {
		super.onResume()
		if (this::auth.isInitialized)
			setupUser(auth.currentUser)
	}
}