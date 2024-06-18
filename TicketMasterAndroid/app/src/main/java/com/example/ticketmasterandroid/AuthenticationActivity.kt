package com.example.ticketmasterandroid

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class AuthenticationActivity : AppCompatActivity() {
    // onCreate function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.authentication_activity)
        // Find the next button
        val nextButton = findViewById<Button>(R.id.next_button)
        // Set click listener
        nextButton.setOnClickListener {
            // Launch MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        // Find the favorite button
        val favoriteButton = findViewById<Button>(R.id.favorites_button)
        // Set click listener
        favoriteButton.setOnClickListener {
            // Launch Favorites Activity
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }
// ----------------------------------------------------------------
        // #### Authentication using FirebaseAuth #####
        // Get instance of the FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser
        // If currentUser is null, open the RegisterActivity
        if (currentUser == null) {
            startRegisterActivity()
        } else {
            // Display user information: username, email address and profile photo (in case Gmail login is used)
            val displayName = currentUser.displayName ?: currentUser.email?.let {
                extractUsernameFromEmail(
                    it
                )
            }
            // display the users name and email
            findViewById<TextView>(R.id.person_name).text = displayName
            findViewById<TextView>(R.id.person_email).text = currentUser.email
            // set and get the photo to display
            Glide.with(this)
                .load(currentUser.photoUrl)
                .placeholder(R.drawable.baseline_account_circle_24)
                .circleCrop()
                .into(findViewById<ImageView>(R.id.person_image))
        }
        // Find the toolbar, which is the top part where exit/sign out button is shown
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        // Set the toolbar as the action bar for this activity
        // Make sure to keep onCreateOptionsMenu and onOptionsItemSelected override functions below
        setSupportActionBar(toolbar)
    } // end on create
// ----------------------------------------------------------------
    // An helper function to start our RegisterActivity
    private fun startRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        // Make sure to call finish() to remove this activity from the backstack, otherwise the user
        // would be able to go back to the MainActivity
        finish()
    }
    // This override function is used to create menu option where you can see on the top right corner
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_options, menu)
        return true
    }
    // Extract username from email (if manually create account from console, name isn't created. so create one from the user email.
    private fun extractUsernameFromEmail(email: String): String {
        return if (email.contains("@")) {
            email.substringBefore("@")
        } else {
            email
        }
    }
// ----------------------------------------------------------------
    // This override function is used to handle if menu_option (logout) is selected.
    // If so, the user will be signed out.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // User chose the "logout" item, logout the user then
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
                AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // After logout, start the RegisterActivity again
                            startRegisterActivity()
                        } else {
                            Log.e(ContentValues.TAG, "Task is not successful:${task.exception}")
                        }
                    }
                true
            }
            else -> {
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                super.onOptionsItemSelected(item)
            }
        }
    } // end options function
} // end main


