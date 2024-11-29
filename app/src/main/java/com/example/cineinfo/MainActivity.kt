package com.example.cineinfo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cineinfo.databinding.ActivityMainBinding
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
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


data class MovieResponse(
    val results: List<Movie> // Lista de filmes
)


data class Movie(
    val id: Int,
    @SerializedName("poster_path") val posterPath: String,
    val title: String,
    val overview: String,
    val genres: List<Genre>
)

interface MovieService {
    @GET("movie/{movie_id}")
    fun getMovie(
        @Path("movie_id") movieId: Int,
        @Header("Authorization") auth: String = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI4ZmYyZGNjZjBkNmM1NDA1NzYxMzk2YTQyYjU3MWI0MCIsIm5iZiI6MTczMjA3MTAwMi40NDAzMDgzLCJzdWIiOiI2NzNiZTk1Zjc3ZWI1OWYyZjAxYTY1YTIiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.gI-Nb9r-Uo7L9EGhWwNh8XcNTBQxC5Lfy1yqV6O7TzM"
    ): Call<Movie>

    @GET("movie/popular")
    fun getPopularMovies(
        @Header("Authorization") auth: String = " Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI4ZmYyZGNjZjBkNmM1NDA1NzYxMzk2YTQyYjU3MWI0MCIsIm5iZiI6MTczMjA3MTAxNy4zMjgxNTI0LCJzdWIiOiI2NzNiZTk1Zjc3ZWI1OWYyZjAxYTY1YTIiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.pq7-utQ6zYd2HG2gCNrU8ghXRGlejZ6TYRKzjEu_cX4"
    ): Call<MovieResponse>

}

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapterMovie: MovieAdapter // Adaptador para a lista de filmes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar o RecyclerView
        adapterMovie = MovieAdapter()
        binding.genreList.adapter = adapterMovie
        binding.genreList.layoutManager = LinearLayoutManager(this)

        // Criar o Retrofit e a instância do MovieService
        val movieService = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieService::class.java)

        // Fazer a requisição para obter os filmes populares
        movieService.getPopularMovies().enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    val movies = response.body()?.results
                    movies?.let {
                        adapterMovie.setMovies(it) // Passar os filmes para o adaptador
                    }
                } else {
                    Log.e("MovieAPI", "Erro na resposta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                Log.e("MovieAPI", "Falha na requisição: ${t.message}")
            }
        })
    }
}
