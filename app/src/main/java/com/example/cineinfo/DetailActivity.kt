package com.example.cineinfo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cineinfo.databinding.ActivityDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

// Definição do serviço para a API
interface MovieDetailService {
    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Header("Authorization") auth: String = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI4ZmYyZGNjZjBkNmM1NDA1NzYxMzk2YTQyYjU3MWI0MCIsIm5iZiI6MTczMjA3MTAwMi40NDAzMDgzLCJzdWIiOiI2NzNiZTk1Zjc3ZWI1OWYyZjAxYTY1YTIiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.gI-Nb9r-Uo7L9EGhWwNh8XcNTBQxC5Lfy1yqV6O7TzM"
    ): Call<MovieDetail>
}

// Dados do filme para a `DetailActivity`
data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String,
    val release_date: String,
    val runtime: Int,
    val revenue: Long,
    val tagline: String,
    val poster_path: String,
    val backdrop_path: String,
    val vote_average: Float,
    val production_companies: List<ProductionCompany>
)

data class ProductionCompany(
    val name: String,
    val origin_country: String
)

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Recuperar o movie_id da Intent
        val movieId = intent.getIntExtra("MOVIE_ID", -1)
        if (movieId == -1) {
            Toast.makeText(this, "Erro: ID do filme não fornecido", Toast.LENGTH_SHORT).show()
            finish() // Finaliza a activity se o ID não for passado
            return
        }

        // Criar o Retrofit e o serviço de detalhes do filme
        val movieService = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieDetailService::class.java)

        // Fazer a requisição para obter os detalhes do filme
        movieService.getMovieDetails(movieId).enqueue(object : Callback<MovieDetail> {
            override fun onResponse(call: Call<MovieDetail>, response: Response<MovieDetail>) {
                if (response.isSuccessful) {
                    val movieDetail = response.body()
                    movieDetail?.let {
                        // Preencher os dados da UI com as informações do filme
                        binding.movieTitle.text = it.title
                        binding.movieOverview.text = it.overview
                        binding.movieReleaseDate.text = it.release_date
                        binding.movieRuntime.text = "${it.runtime} min"
                        binding.movieRevenue.text = "$${it.revenue}"
                        binding.movieTagline.text = it.tagline
                        binding.movieVoteAverage.text = "Rating: ${it.vote_average}"

                        // Exibir a imagem do poster
                        val posterUrl = "https://image.tmdb.org/t/p/w500${it.poster_path}"
                        Glide.with(this@DetailActivity).load(posterUrl).into(binding.moviePoster)

                        // Exibir a imagem de backdrop (opcional)
                        val backdropUrl = "https://image.tmdb.org/t/p/w500${it.backdrop_path}"
                        Glide.with(this@DetailActivity).load(backdropUrl).into(binding.movieBackdrop)

                        // Exibir as companhias de produção
                        val productionCompanies = it.production_companies.joinToString(", ") { company ->
                            company.name
                        }
                        binding.movieProductionCompanies.text = "Production Companies: $productionCompanies"
                    }
                } else {
                    Toast.makeText(this@DetailActivity, "Erro ao carregar os detalhes do filme", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MovieDetail>, t: Throwable) {
                Toast.makeText(this@DetailActivity, "Falha na requisição: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}