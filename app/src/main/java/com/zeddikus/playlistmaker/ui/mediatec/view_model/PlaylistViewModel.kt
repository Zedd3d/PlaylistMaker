package com.zeddikus.playlistmaker.ui.mediatec.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeddikus.playlistmaker.domain.db.PlaylistsInteractor
import com.zeddikus.playlistmaker.domain.mediatec.playlists.models.Playlist
import com.zeddikus.playlistmaker.ui.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val state = MutableLiveData<Boolean>()
    private var updateRenderJob: Job? = null
    private var filenameCover = ""
    private val canGoBack = SingleLiveEvent<Boolean>()
    private val closeFragment = SingleLiveEvent<Boolean>()

    companion object {
        private const val UPDATE_RENDER_DELAY = 300L
    }

    fun getState(): LiveData<Boolean> = state
    fun getCanGoBack(): SingleLiveEvent<Boolean> = canGoBack
    fun getCloseFragment(): SingleLiveEvent<Boolean> = closeFragment

    fun changeText() {
        updateRenderJob?.cancel()
        updateRenderJob = viewModelScope.launch {
            delay(UPDATE_RENDER_DELAY)
            state.postValue(true)
        }
    }

    fun insertPlaylist(
        name: String,
        desc: String
    ) {
        val playlist = Playlist(
            0,
            name,
            desc,
            filenameCover,
            0,
            System.currentTimeMillis()
        )
        viewModelScope.launch {
            playlistsInteractor.insertPlaylist(playlist)
        }

        closeFragment.postValue(true)
    }

    fun saveFilenameCover(filename: String) {
        filenameCover = filename
    }

    fun checkCanGoBack(notEmptyName: Boolean, notEmptyDesc: Boolean) {
        canGoBack.postValue(
            !(notEmptyName ||
                    notEmptyDesc ||
                    filenameCover.isNotEmpty())
        )
    }

}