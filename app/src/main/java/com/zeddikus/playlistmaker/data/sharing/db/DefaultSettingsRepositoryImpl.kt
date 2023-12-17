package com.zeddikus.playlistmaker.data.sharing.db

import com.zeddikus.playlistmaker.domain.settings.model.EmailData
import com.zeddikus.playlistmaker.domain.sharing.api.DefaultSettingsRepository

class DefaultSettingsRepositoryImpl(
    private val supportEmail: String,
    private val thanksTemplateSubj: String,
    private val thanksTemplateBody: String,
    private val ypLinkAd: String,
    private val ypLinkTerms: String,
) : DefaultSettingsRepository {

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