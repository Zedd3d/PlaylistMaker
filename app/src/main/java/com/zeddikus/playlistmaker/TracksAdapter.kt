package com.zeddikus.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class TracksAdapter(
    private var tracks: List<Track>,
    private var searchHistoryHandler: SearchHistoryHandler?
) : RecyclerView.Adapter<TracksViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.track_element, parent, false)
        view.setOnClickListener{
            if (it.context is SearchActivity) {
                val searchActivity = (it.context as SearchActivity)
                var track = this.getItem(
                    if (searchHistoryHandler == null)
                        searchActivity.recyclerHistory.getChildAdapterPosition(it)
                    else
                        searchActivity.recyclerTracks.getChildAdapterPosition(it)
                )

                searchHistoryHandler?.addTrackToHistory(track)
                searchActivity.showPlayer(track)
            } else {
                Toast.makeText(it.context,"Передан неподходящий контекст",Toast.LENGTH_SHORT)
            }
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