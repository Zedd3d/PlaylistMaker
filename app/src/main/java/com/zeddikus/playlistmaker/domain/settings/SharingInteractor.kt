package com.zeddikus.playlistmaker.domain.settings

interface SharingInteractor {
    fun shareApp()
    fun showTerms()
    fun mailToSupport()

    fun sharePlaylist(content: String)
}