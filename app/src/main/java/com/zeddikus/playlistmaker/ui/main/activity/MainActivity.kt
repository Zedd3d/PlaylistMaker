package com.zeddikus.playlistmaker.ui.main.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.zeddikus.playlistmaker.databinding.ActivityMainBinding
import com.zeddikus.playlistmaker.ui.mediatec.activity.MediatecActivity
import com.zeddikus.playlistmaker.ui.search.activity.SearchActivity
import com.zeddikus.playlistmaker.ui.settings.activity.SettingsActivity

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)

        val buttonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (v != null) {
                    startSearchActivity()
                }
            }
        }
        binding.btnSearch.setOnClickListener(buttonClickListener)
        binding.btnMediatec.setOnClickListener {
            val intent = Intent(this, MediatecActivity::class.java)
            startActivity(intent)
        }

        binding.btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startSearchActivity() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }
}