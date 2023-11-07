package com.zeddikus.playlistmaker.presentation.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toast
import com.zeddikus.playlistmaker.Creator
import com.zeddikus.playlistmaker.R

class SettingsActivity : AppCompatActivity(), View.OnClickListener{

    private val themeChanger = Creator.provideThemeChanger()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<LinearLayout>(R.id.llShare).setOnClickListener(this)
        findViewById<LinearLayout>(R.id.llSupport).setOnClickListener(this)
        findViewById<LinearLayout>(R.id.llTerms).setOnClickListener(this)
        val switchTheme = findViewById<Switch>(R.id.swBlackTheme)

        switchTheme.isChecked = themeChanger.getCurrentTheme()

        switchTheme.setOnClickListener{
            themeChanger.saveSwitchDarkTheme((it as Switch).isChecked)
        }

        val btnBack = findViewById<ImageButton>(R.id.imgBtnBack)
        btnBack.setOnClickListener{
            finish()
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id ) {
                R.id.swBlackTheme -> Toast.makeText(this,resources.getText(R.string.switch_to_dark_theme),Toast.LENGTH_SHORT).show()
                R.id.llShare -> shareApp()
                R.id.llSupport -> mailToSupport()
                R.id.llTerms -> showTerms()
                //else -> println("Другое")
            }
        }
    }

    private fun showTerms() {
        val url = Uri.parse(resources.getString(R.string.YP_LINK_OFFER))
        val intent = Intent(Intent.ACTION_VIEW, url)
        startActivity(intent)
    }

    private fun mailToSupport() {
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(resources.getString(R.string.MAIL_TO))
            putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.thanks_template_subj))
            putExtra(Intent.EXTRA_EMAIL, arrayOf(resources.getString(R.string.DEFAULT_EMAIL)))
            putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.thanks_template_body))
            startActivity(this)
        }
    }

    private fun shareApp() {
        val sendIntent: Intent = Intent(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT,resources.getString(R.string.YP_LINK_AD))
        sendIntent.type = resources.getString(R.string.TEXT_PLAIN)

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}