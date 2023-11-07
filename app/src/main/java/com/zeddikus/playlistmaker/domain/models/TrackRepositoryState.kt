package com.zeddikus.playlistmaker.domain.models

enum class TrackRepositoryState {
    GONE,
    ERROR_NETWORK,
    ERROR_EMPTY,
    OK,
    SHOW_HISTORY,
    SEARCH_IN_PROGRESS;
}