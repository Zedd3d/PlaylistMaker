package com.zeddikus.playlistmaker.domain.settings.api

interface ThemeChanger {
    fun saveSwitchDarkTheme(darkThemeEnabled: Boolean)

    fun getCurrentTheme(): Boolean
}