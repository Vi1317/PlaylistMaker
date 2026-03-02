package com.example.playlistmaker.search.data.dto

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackListItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TrackAdapter (private val tracks: List<Track>) : RecyclerView.Adapter<TrackViewHolder>() {
    var onTrackClick: ((Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = TrackListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            onTrackClick?.invoke(tracks[position])
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}

class TrackViewHolder(private val binding: TrackListItemBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Track) {
        binding.trackTitle.text = item.trackName.trim()
        binding.trackArtist.text = item.artistName.trim()
        binding.trackArtist.requestLayout()
        binding.trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTimeMillis).trim()
        binding.root.requestLayout()
        Glide.with(binding.root)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .into(binding.trackCover)
    }

    companion object {
        fun from(parent: ViewGroup): TrackViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = TrackListItemBinding.inflate(inflater, parent, false)
            return TrackViewHolder(binding)
        }
    }
}