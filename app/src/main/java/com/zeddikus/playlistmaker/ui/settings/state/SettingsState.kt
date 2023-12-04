package com.zeddikus.playlistmaker.ui.settings.state

sealed interface SettingsState {
    data class Content(val isDarkTheme: Boolean) : SettingsState

}