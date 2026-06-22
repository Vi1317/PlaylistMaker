package com.example.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistListItemBinding
import com.example.playlistmaker.media.domain.models.Playlist

class PlaylistBottomSheetAdapter(
    private var playlists: List<Playlist>,
    private val onItemClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistBottomSheetAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PlaylistListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(playlists[position], onItemClick)
    }

    override fun getItemCount(): Int = playlists.size

    fun updatePlaylists(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    class ViewHolder(
        private val binding: PlaylistListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist, onItemClick: (Playlist) -> Unit) {
            binding.playlistName.text = playlist.name
            val trackCountText = itemView.context.resources.getQuantityString(
                R.plurals.track_count,
                playlist.trackCount,
                playlist.trackCount
            )

            binding.trackCount.text = trackCountText

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