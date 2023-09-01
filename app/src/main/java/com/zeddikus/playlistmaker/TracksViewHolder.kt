package com.zeddikus.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TracksViewHolder(parentView: View) : RecyclerView.ViewHolder(parentView) {
    private val artwork: ImageView
    private val trackName: TextView
    private val artistName: TextView
    private val trackTime: TextView

    init {
        
        artwork = parentView.findViewById(R.id.imgArtworkInSearch)
        trackName = parentView.findViewById(R.id.trackNameInSearch)
        artistName = parentView.findViewById(R.id.artistNameInSearch)
        trackTime = parentView.findViewById(R.id.trackTimeInSearch)

        parentView.setOnClickListener{
            var track = (it.context as SearchActivity)
                .adapter.getItem(
                            (it.context as SearchActivity).recyclerTracks.getChildAdapterPosition(it)
                )
            (parentView.context as SearchActivity).adapter.getItem((it.context as SearchActivity).recyclerTracks.getChildAdapterPosition(it))
            Toast.makeText(it.context,"Играет '${track.trackName}'\nАртист '${track.artistName}'",
                    Toast.LENGTH_SHORT).show()

        }
    }

    fun bind(track: Track) {
        Glide.with(artwork)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder_track_artwork)
            .fitCenter()
            .transform(RoundedCorners(General.dpToPx(2f,artwork.context)))
            .into(artwork)
        trackName.text = track.trackName.trim()
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)
        artistName.text= track.artistName.trim()
        artistName.updateLayoutParams { width = 0 }
    }


}