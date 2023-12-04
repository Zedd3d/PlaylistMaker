package com.zeddikus.playlistmaker.data.search.dto

import com.zeddikus.playlistmaker.domain.search.model.TrackRepositoryState
import com.zeddikus.playlistmaker.domain.sharing.model.Track

data class TrackSearchResult (val state: TrackRepositoryState, val listTracks: List<Track>)