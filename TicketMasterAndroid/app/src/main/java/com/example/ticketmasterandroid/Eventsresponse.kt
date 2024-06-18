package com.example.ticketmasterandroid

data class EventsResponse(
    val _embedded: Embedded
)

data class Embedded(
    val events: List<Event>
)