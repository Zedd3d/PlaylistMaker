package com.zeddikus.playlistmaker.data.settings.impl

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.zeddikus.playlistmaker.R
import com.zeddikus.playlistmaker.domain.settings.api.ExternalNavigator
import com.zeddikus.playlistmaker.domain.settings.model.EmailData

class ExternalNavigatorImpl(val application: Application) : ExternalNavigator {
    override fun shareApp(shareAppLink: String) {
        val sendIntent: Intent = Intent(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareAppLink)
        sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        sendIntent.type = application.resources.getString(R.string.TEXT_PLAIN)

        val shareIntent = Intent.createChooser(sendIntent, null)
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        (application as Context).startActivity(shareIntent)
    }

    override fun showTerms(termsLink: String) {
        val url = Uri.parse(termsLink)
        val intent = Intent(Intent.ACTION_VIEW, url)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        (application as Context).startActivity(intent)
    }

    override fun mailToSupport(supportEmailData: EmailData) {
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(application.resources.getString(R.string.MAIL_TO))
            putExtra(Intent.EXTRA_SUBJECT, supportEmailData.subject)
            putExtra(Intent.EXTRA_EMAIL, supportEmailData.email)
            putExtra(Intent.EXTRA_TEXT, supportEmailData.text)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            (application as Context).startActivity(this)
        }
    }
}