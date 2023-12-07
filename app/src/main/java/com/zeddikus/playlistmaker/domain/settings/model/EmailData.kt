package com.zeddikus.playlistmaker.domain.settings.model

data class EmailData(
    val email: Array<String>,
    val subject: String,
    val text: String,
)
