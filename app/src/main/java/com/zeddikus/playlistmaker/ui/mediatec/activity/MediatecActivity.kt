package com.zeddikus.playlistmaker.ui.mediatec.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.zeddikus.playlistmaker.R
import com.zeddikus.playlistmaker.databinding.ActivityMediatecBinding
import com.zeddikus.playlistmaker.ui.mediatec.adapter.MediatecViewPagerAdapter
import com.zeddikus.playlistmaker.ui.mediatec.view_model.MediatecViewModel
import org.koin.android.ext.android.inject

class MediatecActivity : AppCompatActivity() {

    private val viewModel: MediatecViewModel by inject()

    private lateinit var tabMediator: TabLayoutMediator

    private lateinit var binding: ActivityMediatecBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediatecBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)
        binding.viewPagerMediatec.adapter = MediatecViewPagerAdapter(
            supportFragmentManager,
            lifecycle
        )

        tabMediator = TabLayoutMediator(
            binding.tabLayoutMediatec,
            binding.viewPagerMediatec
        ) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.favorites_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabMediator.attach()

        binding.imgBtnBack.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}