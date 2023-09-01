package com.zeddikus.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TracksAdapter(
    private var tracks: List<Track>
) : RecyclerView.Adapter<TracksViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.track_element, parent, false)
        return TracksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun setNewList(newList: List<Track>) {
        tracks = newList
        notifyDataSetChanged()
    }

    fun getItem(pos: Int): Track {
        return tracks.get(pos)
    }

    fun clearList(){
        tracks = emptyList<Track>()
        notifyDataSetChanged()
    }

}