package com.zeddikus.playlistmaker.data.dto

import com.zeddikus.playlistmaker.domain.models.TrackRepositoryState
import com.zeddikus.playlistmaker.domain.models.Track

data class TrackSearchResult (val state: TrackRepositoryState, val listTracks: List<Track>)