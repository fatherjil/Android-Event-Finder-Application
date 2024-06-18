package com.example.ticketmasterandroid


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class EventAdapter(private val context: Context, private val eventList: List<Event>) :
    RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    // create view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflate the layout for the single item list (user_row)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_row, parent, false)
        return ViewHolder(view)
    }

    // bind data to viewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // get the event at position
        val event = eventList[position]
        // bind event to view holder
        holder.bind(event)
        // Set OnClickListener for the eventDescriptionButton to open EventDescriptionActivity
        holder.eventDescriptionButton.setOnClickListener {
            val intent = Intent(context, EventDescriptionActivity::class.java)
            // when pressing the button, event name will be added
            intent.putExtra("eventName", event.name)
            // start activity
            context.startActivity(intent)
        }
    }

    // get item count
    override fun getItemCount(): Int {
        // size of the event list
        return eventList.size
    }

    // VIEW HOLDER class
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // view from user_row.xml
        private val eventName: TextView = itemView.findViewById(R.id.eventNameText)
        private val eventImage: ImageView = itemView.findViewById(R.id.person_icon)
        private val buttonTicket: Button = itemView.findViewById(R.id.buttonTicket)
        private val dateTextView: TextView = itemView.findViewById(R.id.dateText)
        private val priceRangeTextView: TextView = itemView.findViewById(R.id.priceRangeText)
        private val venueNameTextView: TextView = itemView.findViewById(R.id.venueText)
        private val venueAddressTextView: TextView = itemView.findViewById(R.id.addressText)

        // view from user_row.xml
        // variable reference to the eventDescriptionButton
        val eventDescriptionButton: Button = itemView.findViewById(R.id.eventDescriptionButton)

        // -----------------------------------------
        // variable reference to the favorite button
        val likeButton: Button = itemView.findViewById(R.id.like_button)

        // -----------------------------------------
        // Initialize click listener for likeButton
        init {
            // like button on Click listener
            likeButton.setOnClickListener {
                // get event at current adapter position
                val event = eventList[adapterPosition]
                // extract the event name
                val eventName = event.name
                // extract the event date
                // check date for events with same names!! if same name but different date, then add.
                val eventDate = event.dates?.start?.localDate ?: event.time?.start?.localDate
                // get current user's ID from firebaseAuth
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                // If the user ID is not null, proceed with Firestore operations
                userId?.let { uid ->
                    // Get a reference to the Firestore database
                    val db = FirebaseFirestore.getInstance()
                    // Query the "Favorites" collection to check if the event is already liked by the user
                    val favoritesRef = db.collection("Favorites")
                        .whereEqualTo("UserID", uid)
                        .whereEqualTo("EventName", eventName)
                        .whereEqualTo("EventDate", eventDate) // Add condition to check event date
                    // Execute the query asynchronously
                    favoritesRef.get()
                        .addOnSuccessListener { documents ->
                            // Check if the query returned any documents (if the event is already liked)
                            if (documents.isEmpty) {
                                // Event not already liked, add it to favorites
                                val newFavoriteRef = db.collection("Favorites").document()
                                // -----------------------------------------
                                // Prepare data to be added to Firestore
                                val data = hashMapOf(
                                    "UserID" to uid,
                                    "EventName" to eventName,
                                    "EventDate" to eventDate,
                                    "Timestamp" to FieldValue.serverTimestamp() // Add timestamp field
                                )
                                // Set the data in Firestore
                                newFavoriteRef.set(data)
                                    .addOnSuccessListener {
                                        // Show a toast indicating that the event was added to favorites
                                        Toast.makeText(
                                            context,
                                            "Event added to favorites",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                // Event already liked, show a toast indicating that it's already in favorites
                                Toast.makeText(
                                    context,
                                    "Event already added to favorites",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } // end onSuccess listener
                } // end if user is not null
            } // end onClickListener
        } // end init


        // -----------------------------------------
        // bind data to views
        fun bind(event: Event) {
            // set event name to the textView
            eventName.text = event.name
            // set venue information
            val venue = event._embedded.venues?.get(0)
            if (venue != null) {
                // If venue information is available, set the venue name and address
                venueNameTextView.text = "${venue.name}, ${venue.city.name}"
                venueAddressTextView.text =
                    "Address: ${venue.address.line1}, ${venue.city.name}, ${venue.state.name}"
            } else {
                // If venue information is not available, indicate that the venue is not available
                venueNameTextView.text = "Venue not available"
                venueAddressTextView.text = ""
            }
// -----------------------------------------
            // set price range information
            // Check if priceRanges is not null and not empty
            if (!event.priceRanges.isNullOrEmpty()) {
                // Get the first price range (assuming there's only one price range per event)
                val priceRange = event.priceRanges[0]
                // Display the price range
                priceRangeTextView.text = "Price Range: $${priceRange.min} - $${priceRange.max} "
                // Make the priceRangeTextView visible
                priceRangeTextView.visibility = View.VISIBLE
            } else {
                // Hide the priceRangeTextView
                priceRangeTextView.visibility = View.GONE
            }
// -----------------------------------------
            // set date and time information and FORMAT !!!
            // Parse the local time string to a Date object
            val localTime = event.dates?.start?.localTime
            val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.US)
            val time = localTime?.let { timeFormatter.parse(it) }

            // Format the Date object to the desired format
            val formattedTime = time?.let {
                val formattedTimeFormatter = SimpleDateFormat("h:mma", Locale.US)
                formattedTimeFormatter.format(it)
            } ?: "Time not available"

            // Parse the local date string to a Date object
            val localDate = event.dates?.start?.localDate
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = localDate?.let { dateFormatter.parse(it) }

            // Format the Date object to the desired format
            val formattedDate = date?.let {
                val formattedDateFormatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)
                formattedDateFormatter.format(it)
            } ?: "Date not available"
            // set to textview
            dateTextView.text = "Date: $formattedDate @ $formattedTime"

// -----------------------------------------
            // load event images using GLIDE LIBRARY and find highest quality
            val highestQualityImage =
                event.images.maxByOrNull { it.width.toInt() * it.height.toInt() }
            if (highestQualityImage != null) {
                Glide.with(context)
                    .load(highestQualityImage.url)
                    .into(eventImage)
            } else {
                // If highestQualityImage is null, load the first image from the event images list
                Glide.with(context)
                    .load(event.images[0].url)
                    .into(eventImage)
            }
            // Set OnClickListener for the buttonTicket to open ticket URL
            buttonTicket.setOnClickListener {
                // Open the ticket URL in a web browser
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.url))
                context.startActivity(intent)
            } // end setOnClick listener

        } // end bind

    } // end viewHolder


} // end class (eventAdapter)




