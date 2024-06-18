package com.example.ticketmasterandroid

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoritesActivity : AppCompatActivity() {

    // Declare RecyclerView, Adapter, FirebaseFirestore, and Button variables
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoritesAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var buttonBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        // Initialize RecyclerView and Button
        recyclerView = findViewById(R.id.recyclerView)
        buttonBack = findViewById(R.id.buttonBack)

        // Set LinearLayoutManager for the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize FirebaseFirestore instance
        db = FirebaseFirestore.getInstance()

        // Get the current user's ID from FirebaseAuth
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        // If the user ID is not null, fetch favorite events from Firestore
        if (userId != null) {
            db.collection("Favorites")
                .whereEqualTo("UserID", userId)
                .get()
                .addOnSuccessListener { documents ->
                    // Extract event names from Firestore documents and create a mutable list
                    val items = documents.mapNotNull { it.getString("EventName") }.toMutableList()

                    // Initialize FavoritesAdapter with the list of favorite event names and FirebaseFirestore instance
                    adapter = FavoritesAdapter(items, db)

                    // Set the adapter for the RecyclerView
                    recyclerView.adapter = adapter
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                }
        } // end if

        buttonBack.setOnClickListener {
            onBackPressed()
        }

    } // end onCreate
} // end class