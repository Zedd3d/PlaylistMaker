package com.zeddikus.playlistmaker.ui.mediatec.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeddikus.playlistmaker.domain.db.PlaylistsInteractor
import com.zeddikus.playlistmaker.domain.mediatec.playlists.models.PlaylistsState
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private var onLoad = MutableLiveData<PlaylistsState>()

    init {
        fillData()
    }

    fun onLoad(): LiveData<PlaylistsState> = onLoad

    fun fillData() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists()
                .collect { playlists ->
                    if (playlists.isEmpty()) {
                        onLoad.postValue(PlaylistsState.Empty)
                    } else {
                        onLoad.postValue(PlaylistsState.ShowListResult(playlists))
                    }
                }
        }
    }

}