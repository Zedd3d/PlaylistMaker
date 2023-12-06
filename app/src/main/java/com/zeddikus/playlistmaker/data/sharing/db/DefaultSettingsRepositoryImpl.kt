package com.zeddikus.playlistmaker.data.sharing.db

import com.zeddikus.playlistmaker.domain.settings.model.EmailData
import com.zeddikus.playlistmaker.domain.sharing.api.DefaultSettingsRepository

object DefaultSettingsRepositoryImpl : DefaultSettingsRepository {
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
        DefaultSettingsRepositoryImpl.supportEmail = supportEmail
        DefaultSettingsRepositoryImpl.thanksTemplateSubj = thanksTemplateSubj
        DefaultSettingsRepositoryImpl.thanksTemplateBody = thanksTemplateBody
        DefaultSettingsRepositoryImpl.ypLinkAd = ypLinkAd
        DefaultSettingsRepositoryImpl.ypLinkTerms = ypLinkTerms
    }

    override fun getShareSupportEmail(): EmailData {
        return EmailData(arrayOf(supportEmail), thanksTemplateSubj, thanksTemplateBody)
    }

    override fun getTermsLink(): String {
        return ypLinkTerms
    }

    override fun getShareAppLink(): String {
        return ypLinkAd
    }

}