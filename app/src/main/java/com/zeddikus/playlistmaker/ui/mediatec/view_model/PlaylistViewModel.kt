package com.zeddikus.playlistmaker.ui.mediatec.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeddikus.playlistmaker.domain.db.PlaylistsInteractor
import com.zeddikus.playlistmaker.domain.mediatec.playlists.models.PlaylistPropertys
import com.zeddikus.playlistmaker.domain.search.model.Track
import com.zeddikus.playlistmaker.domain.settings.SharingInteractor
import com.zeddikus.playlistmaker.ui.SingleLiveEvent
import com.zeddikus.playlistmaker.utils.General
import com.zeddikus.playlistmaker.utils.debounce
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistViewModel(
    playlistId: Int,
    private val playlistsInteractor: PlaylistsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val state = MutableLiveData<PlaylistPropertys>()
    private val showPlayer = SingleLiveEvent<Track>()
    private val editPlaylist = SingleLiveEvent<Int?>()
    private val onTrackClickDebounce =
        debounce<Track>(STANDART_DELAY, viewModelScope, false) { track ->
            showPlayer.postValue(track)
        }

    companion object {
        private const val STANDART_DELAY = 300L
    }

    init {
        viewModelScope.launch {
            renderPlaylist(playlistId)
        }
    }

    suspend fun renderPlaylist(playlistId: Int) {
        val playlist = playlistsInteractor.getPlaylistById(playlistId)
        playlistsInteractor.tracksInPlaylist(playlist).collect {
            val allTime =
                (SimpleDateFormat("mm", Locale.getDefault()).format(playlist.playlistTimeMillis)
                    ?: "00").toInt()

            state.postValue(
                PlaylistPropertys(
                    it,
                    playlist,
                    "${allTime} ${General.declinationMinutes(allTime)}",
                    "${playlist.tracksCount} ${General.declinationTracksCount(playlist.tracksCount)}"
                )
            )
        }
    }

    fun update() {
        viewModelScope.launch {
            state.value?.playlist?.let { renderPlaylist(it.playlistId) }
        }
    }

    fun getState(): LiveData<PlaylistPropertys> = state

    fun getShowPlayerTrigger(): SingleLiveEvent<Track> = showPlayer
    fun getEditPlaylistTrigger(): SingleLiveEvent<Int?> = editPlaylist

    fun showPlayer(track: Track) {
        onTrackClickDebounce(track)
    }

    fun deleteTrackFromPlaylist(track: Track) {
        viewModelScope.launch {

            state.value?.playlist?.let {
                playlistsInteractor.deleteTrackFromPlaylist(it, track)
                renderPlaylist(it.playlistId)
            }

        }
    }

    fun sharePlaylist() {
        val sb = StringBuilder()
        state.value?.playlist?.let {
            val playlist = it

            sb.append(playlist.playlistName)
            sb.append("\n${playlist.playlistDescription}")
            sb.append("\n${state.value?.trackCount ?: "0 треков"}")

            var n = 0
            state.value?.tracks?.forEach {
                n++
                sb.append("\n$n. ${it.artistName} - ${it.trackName} (${it.trackTime})")
            }

            sharingInteractor.sharePlaylist(sb.toString())
        }

    }

    fun deletePlaylist() {
        viewModelScope.launch {
            state.value?.playlist?.let {
                playlistsInteractor.deletePlaylistById(it.playlistId)
            }
        }
    }

    fun editPlaylistSettings() {
        editPlaylist.postValue(state.value?.playlist?.playlistId)
    }


}