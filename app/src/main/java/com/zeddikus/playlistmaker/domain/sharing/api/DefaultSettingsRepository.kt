package com.zeddikus.playlistmaker.domain.sharing.api

import com.zeddikus.playlistmaker.domain.settings.model.EmailData

interface DefaultSettingsRepository {
    fun setDefaultValues(
        supportEmail: String,
        thanksTemplateSubj: String,
        thanksTemplateBody: String,
        ypLinkAd: String,
        ypLinkTerms: String
    )

    fun getShareAppLink(): String
    fun getShareSupportEmail(): EmailData
    fun getTermsLink(): String
}