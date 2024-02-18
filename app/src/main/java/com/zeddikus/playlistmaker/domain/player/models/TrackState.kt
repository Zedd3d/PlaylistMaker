package com.zeddikus.playlistmaker.domain.player.models

import com.zeddikus.playlistmaker.domain.search.model.Track

data class TrackState(val track: Track, val isFavorite: Boolean)