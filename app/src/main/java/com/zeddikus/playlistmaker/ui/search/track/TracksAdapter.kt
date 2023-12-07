package com.zeddikus.playlistmaker.ui.search.track

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zeddikus.playlistmaker.R
import com.zeddikus.playlistmaker.domain.sharing.model.Track

class TracksAdapter(
    private var tracks: List<Track>,
    private val clickListener: (track: Track) -> Unit
) : RecyclerView.Adapter<TracksViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.track_element, parent, false)

        return TracksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            clickListener.invoke(tracks[position])
        }
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun setNewList(newList: List<Track>) {
        tracks = newList
        notifyDataSetChanged()
    }

    fun clearList(){
        tracks = emptyList<Track>()
        notifyDataSetChanged()
    }

}

