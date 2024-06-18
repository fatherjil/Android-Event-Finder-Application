package com.example.ticketmasterandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

// Adapter for displaying favorite items in a RecyclerView
class FavoritesAdapter(private var items: MutableList<String>, private val db: FirebaseFirestore) :
    RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {

    // Create View Holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the item layout and create the ViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite, parent, false)
        return ViewHolder(view)
    } // end create

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textTitle.text = item
        // Set click listener for the delete button
        holder.buttonDelete.setOnClickListener {
            deleteItem(position)
        }
    } // end bind

    // Get total number of items in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Delete item from the list and Firestore database
    private fun deleteItem(position: Int) {
        // Get the name of the item to be deleted
        val itemName = items[position]
        // Query Firestore to find documents with matching item name
        db.collection("Favorites")
            .whereEqualTo("EventName", itemName)
            .get()
            .addOnSuccessListener { documents ->
                // Check if any documents match the query
                if (!documents.isEmpty) {
                    // Get the first document
                    val document = documents.documents.firstOrNull()
                    document?.reference?.delete()
                        ?.addOnSuccessListener {
                            // If deletion from Firestore is successful, remove item from the list
                            items.removeAt(position) // Remove item from the list
                            notifyDataSetChanged() // Notify adapter of data change
                        }
                        ?.addOnFailureListener { exception ->
                            // Handle failure
                        }
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    } // end deleteItem function

    // ViewHolder class for caching View components of the item layout
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)
    }
} // end adapter
