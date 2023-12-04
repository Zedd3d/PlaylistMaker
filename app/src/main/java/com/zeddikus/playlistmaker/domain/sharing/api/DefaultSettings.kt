package com.zeddikus.playlistmaker.domain.sharing.api

interface DefaultSettings {
    fun setDefaultValues(
        supportEmail: String,
        thanksTemplateSubj: String,
        thanksTemplateBody: String,
        ypLinkAd: String,
        ypLinkTerms: String
    )
}