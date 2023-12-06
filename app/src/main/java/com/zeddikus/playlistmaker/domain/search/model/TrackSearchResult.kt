package com.zeddikus.playlistmaker.domain.search.model

import com.zeddikus.playlistmaker.domain.sharing.model.Track

data class TrackSearchResult (val state: TrackRepositoryState, val listTracks: List<Track>)