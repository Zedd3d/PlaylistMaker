package com.zeddikus.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class TracksAdapter(
    private var tracks: List<Track>,
    private var searchHistoryHandler: SearchHistoryHandler?
) : RecyclerView.Adapter<TracksViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.track_element, parent, false)
        view.setOnClickListener{
            var track = this.getItem(
                    if (searchHistoryHandler==null)
                        (it.context as SearchActivity).recyclerHistory.getChildAdapterPosition(it)
                    else
                        (it.context as SearchActivity).recyclerTracks.getChildAdapterPosition(it)
                )
            Toast.makeText(it.context,"Играет '${track.trackName}'\nАртист '${track.artistName}'",
                Toast.LENGTH_SHORT).show()
            searchHistoryHandler?.addTrackToHistory(track)
        }
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