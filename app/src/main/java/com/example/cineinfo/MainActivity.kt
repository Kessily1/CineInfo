package com.example.cineinfo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cineinfo.databinding.ActivityMainBinding
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import kotlin.math.log

data class Genre(
    val id: Int,
    val name: String
)

data class  Movie(
    val id: Int,
    @SerializedName("backdrop_path")
    val backDorp: String,
    val title: String,
    val genres: List<Genre>
)

interface MovieService {
    @GET("movie/{movie_id}")
    fun getMovie(@Path("movie_id") movieId: Int,
                 @Header("Authorization") auth: String = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI4ZmYyZGNjZjBkNmM1NDA1NzYxMzk2YTQyYjU3MWI0MCIsIm5iZiI6MTczMjA3MTAwMi40NDAzMDgzLCJzdWIiOiI2NzNiZTk1Zjc3ZWI1OWYyZjAxYTY1YTIiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.gI-Nb9r-Uo7L9EGhWwNh8XcNTBQxC5Lfy1yqV6O7TzM"
    ): Call<Movie>
}

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapterGenre: GenreAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val movieService = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieService::class.java)

        adapterGenre = GenreAdapter{ id ->
            Toast.makeText(this, id, Toast.LENGTH_SHORT).show()

        }
        binding.genreList.adapter = adapterGenre
        binding.genreList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        movieService.getMovie( 1034541 ).enqueue(object: retrofit2.Callback<Movie> {
            override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                if (response.isSuccessful) {
                    val movie = response.body()

                    if (movie != null) {
                        binding.title.text = movie.title
                        adapterGenre.setGenres(movie.genres)
                        Toast.makeText(this@MainActivity, movie.title, Toast.LENGTH_SHORT).show()
                        Log.d("test", movie.toString())
                    }
                } else {
                    Log.d("test", response.toString())
                }
            }
            override fun onFailure(call: Call<Movie>, t: Throwable) {
                t.printStackTrace()
                Log.d(  "test" , t.message.toString())
            }
        })

    }
    }

/*
*
* {
  "adult": false,
  "backdrop_path": "/3V4kLQg0kSqPLctI5ziYWabAZYF.jpg",
  "belongs_to_collection": {
    "id": 558216,
    "name": "Venom Collection",
    "poster_path": "/4bXIKqdZIjR8wKgZaGDaLhLj4yF.jpg",
    "backdrop_path": "/vq340s8DxA5Q209FT8PHA6CXYOx.jpg"
  },
  "budget": 120000000,
  "genres": [
    {
      "id": 878,
      "name": "Science Fiction"
    },
    {
      "id": 28,
      "name": "Action"
    },
    {
      "id": 12,
      "name": "Adventure"
    }
  ],
  "homepage": "https://venom.movie",
  "id": 912649,
  "imdb_id": "tt16366836",
  "origin_country": [
    "US"
  ],
  "original_language": "en",
  "original_title": "Venom: The Last Dance",
  "overview": "Eddie and Venom are on the run. Hunted by both of their worlds and with the net closing in, the duo are forced into a devastating decision that will bring the curtains down on Venom and Eddie's last dance.",
  "popularity": 3777.259,
  "poster_path": "/aosm8NMQ3UyoBVpSxyimorCQykC.jpg",
  "production_companies": [
    {
      "id": 5,
      "logo_path": "/71BqEFAF4V3qjjMPCpLuyJFB9A.png",
      "name": "Columbia Pictures",
      "origin_country": "US"
    },
    {
      "id": 84041,
      "logo_path": "/nw4kyc29QRpNtFbdsBHkRSFavvt.png",
      "name": "Pascal Pictures",
      "origin_country": "US"
    },
    {
      "id": 53462,
      "logo_path": "/nx8B3Phlcse02w86RW4CJqzCnfL.png",
      "name": "Matt Tolmach Productions",
      "origin_country": "US"
    },
    {
      "id": 91797,
      "logo_path": null,
      "name": "Hutch Parker Entertainment",
      "origin_country": "US"
    },
    {
      "id": 14439,
      "logo_path": null,
      "name": "Arad Productions",
      "origin_country": "US"
    }
  ],
  "production_countries": [
    {
      "iso_3166_1": "US",
      "name": "United States of America"
    }
  ],
  "release_date": "2024-10-22",
  "revenue": 394000000,
  "runtime": 109,
  "spoken_languages": [
    {
      "english_name": "English",
      "iso_639_1": "en",
      "name": "English"
    }
  ],
  "status": "Released",
  "tagline": "'Til death do they part.",
  "title": "Venom: The Last Dance",
  "video": false,
  "vote_average": 6.456,
  "vote_count": 758
}
*
* */