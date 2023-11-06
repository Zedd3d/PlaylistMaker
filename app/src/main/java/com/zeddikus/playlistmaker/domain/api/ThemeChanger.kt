package com.zeddikus.playlistmaker.domain.api

interface ThemeChanger {
    fun saveSwitchDarkTheme(darkThemeEnabled: Boolean)

    fun getCurrentTheme(): Boolean
}