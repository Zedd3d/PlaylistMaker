package com.zeddikus.playlistmaker.domain.settings.api

import com.zeddikus.playlistmaker.domain.settings.model.EmailData

interface ExternalNavigator {
    fun shareApp(shareAppLink: String)
    fun showTerms(termsLink: String)
    fun mailToSupport(supportEmailData: EmailData)
    fun sharePlaylist(content: String)
}