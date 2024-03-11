package com.zeddikus.playlistmaker.ui.mediatec.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zeddikus.playlistmaker.R
import com.zeddikus.playlistmaker.domain.mediatec.playlists.models.Playlist

class PlaylistsAdapter(
    private var playlists: List<Playlist>,
    private var bigSizeIcon: Boolean,
    private val clickListener: (playlist: Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val view = if (bigSizeIcon) {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.playlist_element_big, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.playlist_element, parent, false)
        }



        return PlaylistsViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            clickListener.invoke(playlists[position])
        }
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun setNewList(newList: List<Playlist>) {
        playlists = newList
        notifyDataSetChanged()
    }


}

