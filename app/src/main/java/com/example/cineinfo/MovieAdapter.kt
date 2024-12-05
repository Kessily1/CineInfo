package com.example.cineinfo

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cineinfo.databinding.MovieCardBinding

class MovieAdapter(private val onclick: (Int) -> Unit) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {
    private val movies = mutableListOf<Movie>()

    fun setMovies(movies: List<Movie>) {
        this.movies.clear()
        this.movies.addAll(movies)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MovieCardBinding.inflate(inflater, parent, false)
        return MovieViewHolder(binding, onclick)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    class MovieViewHolder(private val binding: MovieCardBinding, private val onclick: (Int) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            // Ação ao clicar no item
            binding.root.setOnClickListener {
                onclick(movie.id) // Passa o ID do filme para o onClick
            }

            // Exibindo informações do filme
            binding.title.text = movie.title
            binding.overview.text = movie.overview

            val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
            Glide.with(binding.root.context).load(imageUrl).into(binding.posterImage)

            // Lógica de favoritos
            var isFavorite = movie.isFavorite

            // Botão de favoritos
            binding.favorite.setOnClickListener {
                isFavorite = !isFavorite

                if (isFavorite) {
                    Toast.makeText(binding.root.context, "${movie.title} adicionado aos favoritos", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(binding.root.context, "${movie.title} removido dos favoritos", Toast.LENGTH_SHORT).show()
                }

                // Atualizar o estado do filme
                movie.isFavorite = isFavorite
            }
        }
    }
}
