package com.zeddikus.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

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
    }

    fun bind(track: Track) {
        Glide.with(artwork)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder_track_artwork)
            .fitCenter()
            .transform(RoundedCorners(dpToPx(2f,artwork.context)))
            .into(artwork)
        trackName.text = track.trackName
        trackTime.text = track.trackTime
        artistName.text= track.artistName
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}