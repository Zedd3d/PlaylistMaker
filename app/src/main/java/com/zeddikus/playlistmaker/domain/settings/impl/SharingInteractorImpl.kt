package com.zeddikus.playlistmaker.domain.settings.impl

import com.zeddikus.playlistmaker.data.sharing.db.DefaultSettingsRepository
import com.zeddikus.playlistmaker.domain.settings.SharingInteractor
import com.zeddikus.playlistmaker.domain.settings.api.ExternalNavigator
import com.zeddikus.playlistmaker.domain.settings.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator
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

    private fun getShareAppLink(): String {
        return DefaultSettingsRepository.getShareAppLink()
    }

    private fun getSupportEmailData(): EmailData {
        return DefaultSettingsRepository.getShareSupportEmail()
    }

    private fun getTermsLink(): String {
        return DefaultSettingsRepository.getTermsLink()
    }
}