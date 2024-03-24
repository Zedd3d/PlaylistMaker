package com.zeddikus.playlistmaker.domain.settings.impl

import com.zeddikus.playlistmaker.domain.settings.SharingInteractor
import com.zeddikus.playlistmaker.domain.settings.api.ExternalNavigator
import com.zeddikus.playlistmaker.domain.settings.model.EmailData
import com.zeddikus.playlistmaker.domain.sharing.api.DefaultSettingsRepository

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val defaultSettingsRepositoryImpl: DefaultSettingsRepository

) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareApp(getShareAppLink())
    }

    override fun showTerms() {
        externalNavigator.showTerms(getTermsLink())
    }

    override fun mailToSupport() {
        externalNavigator.mailToSupport(getSupportEmailData())
    }

    override fun sharePlaylist(content: String) {
        externalNavigator.sharePlaylist(content)
    }

    private fun getShareAppLink(): String {
        return defaultSettingsRepositoryImpl.getShareAppLink()
    }

    private fun getSupportEmailData(): EmailData {
        return defaultSettingsRepositoryImpl.getShareSupportEmail()
    }

    private fun getTermsLink(): String {
        return defaultSettingsRepositoryImpl.getTermsLink()
    }
}