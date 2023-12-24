package com.zeddikus.playlistmaker.domain.sharing.api

import com.zeddikus.playlistmaker.domain.settings.model.EmailData

interface DefaultSettingsRepository {
    fun getShareAppLink(): String
    fun getShareSupportEmail(): EmailData
    fun getTermsLink(): String
}