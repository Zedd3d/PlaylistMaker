package com.zeddikus.playlistmaker.ui.mediatec.playlists

import android.graphics.drawable.Drawable
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.zeddikus.playlistmaker.R
import com.zeddikus.playlistmaker.domain.mediatec.playlists.models.Playlist
import com.zeddikus.playlistmaker.utils.General
import java.io.File

class PlaylistsViewHolder(parentView: View) : RecyclerView.ViewHolder(parentView) {
    private val cover: ImageView
    private val playlistName: TextView
    private val countTracks: TextView
    private val externalFilesDir: File?
    private val drawableByDefault: Drawable
//    private val trackTime: TextView

    init {
        cover = parentView.findViewById(R.id.sivCover)
        playlistName = parentView.findViewById(R.id.tvPlaylistName)
        countTracks = parentView.findViewById(R.id.tvCountTracks)
        externalFilesDir = parentView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        drawableByDefault = parentView.context.getDrawable(R.drawable.placeholder_track_artwork)!!
    }

    fun bind(playlist: Playlist) {

        if (playlist.playlistCover.isNotEmpty()) {
            val filePath = File(externalFilesDir, "album_playlist_covers")
            val file = File(filePath, playlist.playlistCover)
            cover.setImageURI(file.toUri())
        } else {
            cover.setImageDrawable(drawableByDefault)
        }
        playlistName.text = playlist.playlistName
        val ct = "${playlist.tracksCount} ${declination(playlist.tracksCount)}"
        countTracks.text = ct

    }

    private fun declination(tracksCount: Int): String {
        return General.declinationTracksCount(tracksCount)
    }

}