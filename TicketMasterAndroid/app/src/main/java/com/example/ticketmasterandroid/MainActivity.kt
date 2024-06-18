package com.example.ticketmasterandroid

// import the TicketmasterService interface from TicketmasterService.kt
import TicketmasterService
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ticketmasterandroid.RetrofitClient.API_KEY
import com.example.ticketmasterandroid.RetrofitClient.BASE_URL
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    // set variables
    private lateinit var ticketMasterAPI: TicketmasterService
    private val eventList: ArrayList<Event> = ArrayList()
    private lateinit var adapter: EventAdapter

    // onCreate function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the RecyclerView adapter
        adapter = EventAdapter(this, eventList)
        // call function to set up recycle
        setupRecyclerView()
        // create retrofit instance
        ticketMasterAPI = createTicketmasterService()

        // Set up buttonSearch click listener
        val buttonSearch = findViewById<Button>(R.id.buttonSearch)
        buttonSearch.setOnClickListener {
            // variables
            val editTextKeyword = findViewById<EditText>(R.id.editTextKeyword)
            val editTextCity = findViewById<EditText>(R.id.editTextCity)
            val keyword = editTextKeyword.text.toString().trim()
            val city = editTextCity.text.toString().trim()
            // Call the hideKeyboard() function when the search button is clicked
            it.hideKeyboard()
            // Check if both keyword and city fields are empty and show alert
            if (keyword.isEmpty() && city.isEmpty()) {
                showAlert(
                    "Search term missing",
                    "Search term cannot be empty. Please enter a keyword."
                )
            } else if (keyword.isEmpty()) {
                showAlert(
                    "Search term missing",
                    "Search term cannot be empty. Please enter a keyword."
                )
            } else if (city.isEmpty()) {
                showAlert("Location missing", "City cannot be empty. Please enter a city.")
            } else {
                // Both fields are filled, proceed with the search
                searchEvents(keyword, city)
            }
        }
        // Set click listener for the back button
        findViewById<Button>(R.id.back_main).setOnClickListener {
            // go back to previous activity
            onBackPressed()
        }

    }


    // Function to show an alert dialog with the given title and message
    private fun showAlert(title: String, message: String) {
        // get the layout with the textview and the X
        val customTitleView = layoutInflater.inflate(R.layout.custom_title, null)
        val titleTextView = customTitleView.findViewById<TextView>(android.R.id.text1)
        titleTextView.text = title // sets the title text
        // use custom to get RED X IMAGE !!
        val closeIconImageView = customTitleView.findViewById<ImageView>(R.id.red)
        closeIconImageView.setImageResource(R.drawable.red_x_transparent) // Set custom red "X" image

        AlertDialog.Builder(this)
            .setCustomTitle(customTitleView) // Set custom title layout
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    // search events function
    private fun searchEvents(keyword: String, city: String) {
        Log.d(TAG, "searchEvents: Keyword: $keyword, City: $city")
        Log.d(TAG, "searchEvents: Making API call...")
        // Clear the existing list before populating with new search results
        eventList.clear()
        // search using the API with parameters
        val call = ticketMasterAPI.searchEvents(keyword = keyword, city = city, apiKey = API_KEY)
        call.enqueue(object : Callback<EventsResponse> {
            override fun onResponse(
                call: Call<EventsResponse>,
                response: Response<EventsResponse>
            ) {
                if (response.isSuccessful) {
                    val eventsResponse = response.body()
                    if (eventsResponse != null && eventsResponse._embedded != null && !eventsResponse._embedded.events.isNullOrEmpty()) {
                        // Events found, hide noResultText and populate RecyclerView
                        findViewById<TextView>(R.id.noResultText).visibility = View.GONE
                        handleSuccessfulResponse(eventsResponse)
                    } else {
                        // No events found, show noResultText
                        findViewById<TextView>(R.id.noResultText).visibility = View.VISIBLE
                    }
                } else {
                    handleFailedResponse()
                }
            }

            override fun onFailure(call: Call<EventsResponse>, t: Throwable) {
                handleFailedResponse()
            }
        })
    } // end search events


    // if successful add to the events list  // also used to debug
    private fun handleSuccessfulResponse(eventsResponse: EventsResponse?) {
        eventsResponse?.let {
            eventList.clear()
            eventList.addAll(it._embedded.events)
            // Sort the eventList based on dates in ascending order
            eventList.sortBy { event -> event.dates?.start?.localDate }
            adapter.notifyDataSetChanged()
            // debug
            Log.d(TAG, "searchEvents: API call successful")
        }
    }

    // if fails function will show toast // used to debug
    private fun handleFailedResponse() {
        // debug
        Log.e(TAG, "searchEvents: API call failed")
        Toast.makeText(this@MainActivity, "Failed to fetch events", Toast.LENGTH_SHORT).show()
    }

    // Create TicketmasterService instance using Retrofit
    private fun createTicketmasterService(): TicketmasterService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(TicketmasterService::class.java)
    }

    // Set up RecyclerView
    private fun setupRecyclerView() {
        // Sort the eventList based on dates in ascending order
        eventList.sortBy { it.dates?.start?.localDate }
        adapter = EventAdapter(this, eventList) // Use the class-level adapter variable
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewEvents)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    // Define the hideKeyboard() function
    private fun View.hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }



} // end main




