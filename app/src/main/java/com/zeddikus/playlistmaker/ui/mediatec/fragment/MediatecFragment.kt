package com.zeddikus.playlistmaker.ui.mediatec.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.zeddikus.playlistmaker.R
import com.zeddikus.playlistmaker.databinding.FragmentMediatecBinding
import com.zeddikus.playlistmaker.ui.main.activity.MainActivity
import com.zeddikus.playlistmaker.ui.mediatec.adapter.MediatecViewPagerAdapter
import com.zeddikus.playlistmaker.ui.mediatec.view_model.MediatecViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediatecFragment : Fragment() {
    private val viewModel by viewModel<MediatecViewModel>()

    private var tabMediator: TabLayoutMediator? = null

    private lateinit var binding: FragmentMediatecBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding = FragmentMediatecBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPagerMediatec.adapter = MediatecViewPagerAdapter(
            childFragmentManager,
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
        tabMediator?.attach()

    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator?.detach()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).animateBottomNavigationView(View.VISIBLE)
    }
}
