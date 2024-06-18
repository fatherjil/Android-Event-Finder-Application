package com.example.ticketmasterandroid

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventDescriptionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_description)

        // Set click listener for the back button
        findViewById<Button>(R.id.backButton).setOnClickListener {
            // go back to previous activity
            onBackPressed()
        }

        // Get event name from intent extras (from EventAdapter.kt)
        val eventName = intent.getStringExtra("eventName")

        // Get event description using Gemini API
        if (eventName != null) {
            fetchEventDescription(eventName)
        }
    } // end on create

    // generate and get the event description from GEMINI API
    private fun fetchEventDescription(eventName: String) {
        // Show loading indicator to user
        findViewById<ProgressBar>(R.id.loadingProgressBar).visibility = View.VISIBLE

        // Initialize the GenerativeModel
        val generativeModel = GenerativeModel(
            // define gemini model name
            modelName = "gemini-pro",
            // api key
            apiKey = "AIzaSyAH4GV8V6_fISUcVMPM-Rf8UgUEtrYT3Aw"
        )

        // Coroutine scope for calling suspend function
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // get event description using Gemini API
                val prompt = "Describe the event titled $eventName. Do not give event details." +
                        "Provide highlights about the event. Provide what attendees can expect. " +
                        "Give history of the event and the events social medias."
                // generate the prompt
                val response = generativeModel.generateContent(prompt)

                // Update textview with fetched event description
                val textView = findViewById<TextView>(R.id.eventDescriptionTextView)
                textView.text = response.text?.let { makeTextBold(it) }
            } finally {
                // Hide loading indicator
                findViewById<ProgressBar>(R.id.loadingProgressBar).visibility = View.GONE
            }
        }
    } // end fetchDescription Function

    // make the text bold when gemini api displays data
    private fun makeTextBold(text: String): SpannableStringBuilder {
        // Initialize a new SpannableStringBuilder to hold the styled text
        val builder = SpannableStringBuilder()

        // Split the input text into segments based on the "**"
        val segments = text.split("**")

        // Variable to track if the current segment should be bold
        var isBold = false

        // Iterate through each segment
        segments.forEach { segment ->
            // If the segment should be bold
            if (isBold) {
                // Apply bold style to the segment and append it to the builder
                builder.append(
                    segment,
                    StyleSpan(Typeface.BOLD),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else {
                // If the segment should not be bold -> append to builder
                builder.append(segment)
            }
            // Toggle the bold flag for the next segment
            isBold = !isBold
        }

        // Return the final styled text
        return builder
    } // end makeTextBold function
} // end class