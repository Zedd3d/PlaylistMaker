package com.zeddikus.playlistmaker.data.sharing.db

import com.zeddikus.playlistmaker.domain.settings.model.EmailData
import com.zeddikus.playlistmaker.domain.sharing.api.DefaultSettings

object DefaultSettingsRepository : DefaultSettings {
    private lateinit var supportEmail: String
    private lateinit var thanksTemplateSubj: String
    private lateinit var thanksTemplateBody: String
    private lateinit var ypLinkAd: String
    private lateinit var ypLinkTerms: String

    override fun setDefaultValues(
        supportEmail: String,
        thanksTemplateSubj: String,
        thanksTemplateBody: String,
        ypLinkAd: String,
        ypLinkTerms: String
    ) {
        DefaultSettingsRepository.supportEmail = supportEmail
        DefaultSettingsRepository.thanksTemplateSubj = thanksTemplateSubj
        DefaultSettingsRepository.thanksTemplateBody = thanksTemplateBody
        DefaultSettingsRepository.ypLinkAd = ypLinkAd
        DefaultSettingsRepository.ypLinkTerms = ypLinkTerms
    }

    fun getShareSupportEmail(): EmailData {
        return EmailData(arrayOf(supportEmail), thanksTemplateSubj, thanksTemplateBody)
    }

    fun getTermsLink(): String {
        return ypLinkTerms
    }

    fun getShareAppLink(): String {
        return ypLinkAd
    }

}