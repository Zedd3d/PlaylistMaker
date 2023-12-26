package com.zeddikus.playlistmaker.ui.mediatec.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.zeddikus.playlistmaker.ui.mediatec.fragment.FavoritesFragment
import com.zeddikus.playlistmaker.ui.mediatec.fragment.PlaylistsFragment

class MediatecViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    companion object {
        const val TAB_COUNT = 2
    }

    override fun getItemCount(): Int {
        return TAB_COUNT
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoritesFragment.newInstance()
            else -> PlaylistsFragment.newInstance()
        }
    }
}