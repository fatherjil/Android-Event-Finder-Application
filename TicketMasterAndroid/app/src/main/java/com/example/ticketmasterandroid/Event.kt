package com.example.ticketmasterandroid


data class Event(
    val name: String,
    val url: String,
    val _embedded: EmbeddedData,
    val venues: List<Venue>?,
    val images: List<Image>,
    val priceRanges: List<PriceRange>,
    val dates: Dates?,
    val time: Times?
)

data class EmbeddedData(
    val venues: List<Venue>?
)

data class Venue(
    val name: String,
    val city: City,
    val state: State,
    val address: Address
)

data class City(
    val name: String
)

data class State(
    val name: String
)

data class Address(
    val line1: String
)

data class Image(
    val ratio: String,
    val url: String,
    val width: Int,
    val height: Int,
    val fallback: Boolean
)

data class Dates(
    val start: Start
)

data class Times(
    val start: Start
)

data class Start(
    val localDate: String,
    val localTime: String
)

data class PriceRange(
    val min: Double,
    val max: Double
)
