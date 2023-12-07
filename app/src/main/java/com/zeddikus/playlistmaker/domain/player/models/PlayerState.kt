package com.zeddikus.playlistmaker.domain.player.models

enum class PlayerState {
    DEFAULT,
    PREPAIRING,
    PREPARED,
    PLAYING,
    PAUSED,
    STOPPED,
    PREPAIRING_ERROR;
}