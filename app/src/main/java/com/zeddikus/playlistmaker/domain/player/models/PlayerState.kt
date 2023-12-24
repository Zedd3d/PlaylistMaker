package com.zeddikus.playlistmaker.domain.player.models

enum class PlayerState {
    PREPAIRING,
    PREPARED,
    PLAYING,
    PAUSED,
    STOPPED,
    PREPAIRING_ERROR;
}