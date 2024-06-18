package com.example.ticketmasterandroid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth


class RegisterActivity : AppCompatActivity() {

    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Get instance of the FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // If currentUser is not null, we have a user and go to AuthenticationActivity
        if (currentUser != null) {
            val intent = Intent(this, AuthenticationActivity::class.java)
            startActivity(intent)
            // Make sure to call finish(), otherwise the user would be able to go back to the RegisterActivity
            finish()
        } else {
            // create a new ActivityResultLauncher to launch the sign-in activity and handle the result
            // When the result is returned, the result parameter will contain the data and resultCode (e.g., OK, Cancelled etc.).
            val signActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                // Handle the result of the sign-in activity
                if (result.resultCode == Activity.RESULT_OK) {
                    // The user has successfully signed in or is a new user
                    val user = FirebaseAuth.getInstance().currentUser
                    Log.d(TAG, "onActivityResult: $user")

                    // Checking for New/Old User (optional--you do not have to show these toast messages)
                    if (user?.metadata?.creationTimestamp == user?.metadata?.lastSignInTimestamp) {
                        // This is a New User
                        Toast.makeText(this, "Welcome New User!", Toast.LENGTH_SHORT).show()
                    } else {
                        // This is a returning user
                        Toast.makeText(this, "Welcome Back!", Toast.LENGTH_SHORT).show()
                    }

                    // Since the user signed in, proceed to AuthenticationActivity
                    startActivity(Intent(this, AuthenticationActivity::class.java))
                    // Make sure to call finish(), otherwise the user would be able to go back to the RegisterActivity
                    finish()
                } else {
                    // Sign-in failed or canceled by the user
                    val response = IdpResponse.fromResultIntent(result.data)
                    if (response == null) {
                        Log.d(TAG, "onActivityResult: the user has cancelled the sign in request")
                    } else {
                        Log.e(TAG, "onActivityResult: ${response.error?.errorCode}")
                    }
                }
            }

            // Login Button
            findViewById<Button>(R.id.login_button).setOnClickListener {
                // Choose authentication providers -- make sure to enable them on your Firebase account first
                val providers = arrayListOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build()
                    // Add more providers if needed
                )

                // Create sign-in intent
                val signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setTosAndPrivacyPolicyUrls("https://example.com", "https://example.com")
                    .setLogo(R.drawable.baseline_account_circle_24)
                    .setAlwaysShowSignInMethodScreen(true) // Use this if you have only one provider and want to see the sign-in page
                    .setIsSmartLockEnabled(false)
                    .build()

                // Launch sign-in Activity with the sign-in intent
                signActivityLauncher.launch(signInIntent)
            }
        }
    }

}