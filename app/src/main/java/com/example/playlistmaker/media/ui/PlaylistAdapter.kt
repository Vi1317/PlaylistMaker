package com.example.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistGridItemBinding
import com.example.playlistmaker.media.domain.models.Playlist

class PlaylistAdapter(
    private val playlists: List<Playlist>,
    private val onItemClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistAdapter.PlaylistViewHolder {
        val binding = PlaylistGridItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position], onItemClick)
    }

    override fun getItemCount(): Int = playlists.size

    class PlaylistViewHolder (
        private val binding: PlaylistGridItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist, onItemClick: (Playlist) -> Unit) {
            binding.playlistName.text = playlist.name
            val trackSum = playlist.trackCount

            val trackWord = when {
                trackSum % 10 == 1 && trackSum % 100 != 11 -> "трек"
                trackSum % 10 in 2..4 && (trackSum % 100 !in 11..14) -> "трека"
                else -> "треков"
            }

            binding.trackCount.text = "$trackSum $trackWord"

            if (playlist.coverPath.isNotEmpty()) {
                Glide.with(binding.root)
                    .load(playlist.coverPath)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .centerCrop()
                    .into(binding.playlistCover)
            } else {
                Glide.with(binding.root)
                    .load(R.drawable.placeholder)
                    .centerCrop()
                    .into(binding.playlistCover)
            }

            binding.root.setOnClickListener {
                onItemClick(playlist)
            }
        }
    }
}