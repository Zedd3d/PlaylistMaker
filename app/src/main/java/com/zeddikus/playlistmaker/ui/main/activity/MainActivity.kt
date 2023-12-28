package com.zeddikus.playlistmaker.ui.main.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.zeddikus.playlistmaker.R
import com.zeddikus.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        val viewRoot = binding.root
//        setContentView(viewRoot)
//
//        val buttonClickListener: View.OnClickListener = object : View.OnClickListener {
//            override fun onClick(v: View?) {
//                if (v != null) {
//                    startSearchActivity()
//                }
//            }
//        }
//        binding.btnSearch.setOnClickListener(buttonClickListener)
//        binding.btnMediatec.setOnClickListener {
//            val intent = Intent(this, MediatecActivity::class.java)
//            startActivity(intent)
//        }
//
//        binding.btnSettings.setOnClickListener {
//            val intent = Intent(this, SettingsActivity::class.java)
//            startActivity(intent)
//        }
    }

//    private fun startSearchActivity() {
//        val intent = Intent(this, SearchActivity::class.java)
//        startActivity(intent)
//    }
}