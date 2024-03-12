package com.zeddikus.playlistmaker.ui.mediatec.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.zeddikus.playlistmaker.R
import com.zeddikus.playlistmaker.databinding.FragmentPlaylistsBinding
import com.zeddikus.playlistmaker.domain.mediatec.playlists.models.Playlist
import com.zeddikus.playlistmaker.domain.mediatec.playlists.models.PlaylistsState
import com.zeddikus.playlistmaker.ui.mediatec.playlists.PlaylistsAdapter
import com.zeddikus.playlistmaker.ui.mediatec.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistsFragment : Fragment() {
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlaylistsViewModel>()
    private lateinit var adapter: PlaylistsAdapter

    companion object {
        fun newInstance() = PlaylistsFragment().apply {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val placeholderBinding = binding.placeholderTrouble
        if (!(activity == null)) {

            placeholderBinding.placeholderTroubleText.text = getText(R.string.playlists_empty)
            placeholderBinding.placeholderTroubleButton.visibility = View.GONE

            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.layoutPlaylists)
            constraintSet.connect(
                placeholderBinding.root.id,
                ConstraintSet.TOP,
                binding.root.id,
                ConstraintSet.TOP,
                0
            )

            constraintSet.connect(
                placeholderBinding.root.id,
                ConstraintSet.START,
                binding.root.id,
                ConstraintSet.START,
                0
            )

            constraintSet.connect(
                placeholderBinding.root.id,
                ConstraintSet.END,
                binding.root.id,
                ConstraintSet.END,
                0
            )
            constraintSet.applyTo(binding.layoutPlaylists)
        }

        binding.btnCreateNewPlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_mediatecFragment_to_playlistFragment
            )
        }

        binding.recyclerPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = PlaylistsAdapter(listOf<Playlist>(), true) { playlist: Playlist ->
            clickListener(
                playlist
            )
        }
        binding.recyclerPlaylists.adapter = adapter

        viewModel.onLoad().observe(viewLifecycleOwner) { state ->
            render(state)
        }

    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> {
                binding.placeholderTrouble.root.visibility = View.VISIBLE
                binding.recyclerPlaylists.visibility = View.GONE
                adapter.setNewList(emptyList<Playlist>())
            }

            is PlaylistsState.ShowListResult -> {
                binding.placeholderTrouble.root.visibility = View.GONE
                binding.recyclerPlaylists.visibility = View.VISIBLE
                adapter.setNewList(state.playlistsList)
            }
        }
    }

    private fun clickListener(playlist: Playlist) {
    }

    override fun onResume() {
        super.onResume()
        viewModel.fillData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}