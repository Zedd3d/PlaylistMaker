package com.zeddikus.playlistmaker.ui.mediatec.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeddikus.playlistmaker.domain.db.PlaylistsInteractor
import com.zeddikus.playlistmaker.domain.mediatec.playlists.models.Playlist
import com.zeddikus.playlistmaker.domain.mediatec.playlists.models.PlaylistSettingsState
import com.zeddikus.playlistmaker.ui.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlaylistSettingsViewModel(
    private val playlistId: Int?,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val state = MutableLiveData<PlaylistSettingsState>()
    private var updateRenderJob: Job? = null
    private var filenameCover = ""
    private val canGoBack = SingleLiveEvent<Boolean>()
    private val closeFragment = SingleLiveEvent<Boolean>()

    companion object {
        private const val UPDATE_RENDER_DELAY = 300L
    }

    init {
        viewModelScope.launch {
            state.postValue(collectState(true))
        }
    }

    fun getState(): LiveData<PlaylistSettingsState> = state
    fun getCanGoBack(): SingleLiveEvent<Boolean> = canGoBack
    fun getCloseFragment(): SingleLiveEvent<Boolean> = closeFragment

    fun changeText() {
        updateRenderJob?.cancel()
        updateRenderJob = viewModelScope.launch {
            delay(UPDATE_RENDER_DELAY)
            state.postValue(collectState(false))
        }
    }

    private suspend fun collectState(onLoad: Boolean): PlaylistSettingsState {
        return if (playlistId == null) {
            PlaylistSettingsState.NewPlaylist
        } else if (!onLoad) {
            PlaylistSettingsState.EditPlaylist
        } else {
            val playlist = playlistsInteractor.getPlaylistById(playlistId)
            if (playlist.playlistCover.isNotEmpty()) saveFilenameCover(playlist.playlistCover)
            PlaylistSettingsState.PlaylistData(playlist)
        }
    }

    fun insertPlaylist(
        name: String,
        desc: String
    ) {
        val playlist = Playlist(
            playlistId ?: 0,
            name,
            desc,
            filenameCover,
            0,
            System.currentTimeMillis()
        )
        viewModelScope.launch {
            if (playlistId == null) {
                playlistsInteractor.insertPlaylist(playlist)
            } else {
                playlistsInteractor.updatePlaylist(playlist)
            }
        }

        closeFragment.postValue(true)
    }

    fun saveFilenameCover(filename: String) {
        filenameCover = filename
    }

    fun checkCanGoBack(notEmptyName: Boolean, notEmptyDesc: Boolean) {
        if (playlistId == null) {
            canGoBack.postValue(
                !(notEmptyName ||
                        notEmptyDesc ||
                        filenameCover.isNotEmpty())
            )
        } else {
            canGoBack.postValue(true)
        }
    }

}