import com.example.ticketmasterandroid.EventsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TicketmasterService {

        @GET("events.json") // Endpoint
        fun searchEvents(
                @Query("keyword") keyword: String,
                @Query("city") city: String,
                @Query("apikey") apiKey: String
        ): Call<EventsResponse>
}

