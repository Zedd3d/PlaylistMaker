package com.zeddikus.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast

class SettingsActivity : AppCompatActivity(), View.OnClickListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val btnBack = findViewById<ImageButton>(R.id.imgBtnBack)
        btnBack.setOnClickListener{
            finish()
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id ) {
                R.id.swBlackTheme -> Toast.makeText(this,"Переключение на Темную тему",Toast.LENGTH_SHORT).show()
                R.id.llShare -> shareApp()
                R.id.llSupport -> mailToSupport()
                R.id.llTerms -> showTerms()
                else -> println("Другое")
            }
        }
    }

    private fun showTerms() {
        val url = Uri.parse("https://yandex.ru/legal/practicum_offer/")
        val intent = Intent(Intent.ACTION_VIEW, url)
        startActivity(intent)
    }

    private fun mailToSupport() {
        val message = "Спасибо разработчикам и разработчицам за крутое приложение!"
        val shareIntent = Intent(Intent.ACTION_SENDTO)
        shareIntent.data = Uri.parse("mailto:")
        shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("yariyan@yandex.ru"))
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
        startActivity(shareIntent)
    }

    private fun shareApp() {
        val sendIntent: Intent = Intent(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT,"https://practicum.yandex.ru/android-developer/")
        sendIntent.type = "text/plain"

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}