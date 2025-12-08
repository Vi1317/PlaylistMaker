package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class TrackAdapter (val tracks: List<Track>) : RecyclerView.Adapter<TrackViewHolder>() {
    var onTrackClick: ((Track) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_list_item, parent, false)
        return TrackViewHolder(view)
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

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val rootLayout: LinearLayout = itemView.findViewById(R.id.rootLayout)
    private val trackName: TextView = itemView.findViewById(R.id.track_title)
    private val artworkUrl100: ImageView = itemView.findViewById(R.id.track_cover)
    private val artistName: TextView = itemView.findViewById(R.id.track_artist)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)

    fun bind(item: Track) {
        trackName.text = item.trackName.trim()
        artistName.text = item.artistName.trim()
        artistName.requestLayout()
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTimeMillis).trim()
        rootLayout.requestLayout()
        Glide.with(itemView.context)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .into(artworkUrl100)
    }
}