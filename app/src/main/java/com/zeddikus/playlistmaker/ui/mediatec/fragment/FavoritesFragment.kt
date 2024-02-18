package com.zeddikus.playlistmaker.ui.mediatec.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.zeddikus.playlistmaker.R
import com.zeddikus.playlistmaker.databinding.FragmentFavoritesBinding
import com.zeddikus.playlistmaker.domain.mediatec.favorites.models.FavoritesState
import com.zeddikus.playlistmaker.domain.search.model.Track
import com.zeddikus.playlistmaker.ui.mediatec.view_model.FavoritesViewModel
import com.zeddikus.playlistmaker.ui.player.activity.PlayerActivity
import com.zeddikus.playlistmaker.ui.search.track.TracksAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TracksAdapter
    private val viewModel by viewModel<FavoritesViewModel>()

    companion object {
        fun newInstance() = FavoritesFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initalizePlaceholder()
        binding.recyclerTracks.layoutManager = LinearLayoutManager(requireContext())
        adapter = TracksAdapter(listOf<Track>()) { track: Track ->
            clickListener(
                track
            )
        }
        binding.recyclerTracks.adapter = adapter
        setListenersWatchersObservers()
    }

    fun initalizePlaceholder() {
        val placeholderBinding = binding.placeholderTrouble
        if (!(activity == null)) {
            placeholderBinding.placeholderTroubleText.text = getText(R.string.favorites_empty)
            placeholderBinding.placeholderTroubleButton.visibility = View.GONE
        }
    }

    private fun setListenersWatchersObservers() {

        viewModel.getState().observe(viewLifecycleOwner) { state ->
            showListState(state)
        }

        viewModel.getShowPlayerTrigger().observe(viewLifecycleOwner) { track ->
            showPlayer(track)
        }

    }

    private fun showListState(state: FavoritesState) {

        binding.placeholderTrouble.placeholderTrouble.visibility = when (state) {
            is FavoritesState.Empty -> View.VISIBLE
            else -> View.GONE
        }
        binding.recyclerTracks.visibility = when (state) {
            is FavoritesState.Content -> {
                adapter.setNewList(state.tracks)
                View.VISIBLE
            }

            else -> {
                View.GONE
            }
        }

        binding.progressBarFavoritesTracks.visibility = when (state) {
            is FavoritesState.Loading -> View.VISIBLE
            else -> View.GONE
        }

        if (binding.recyclerTracks.visibility == View.GONE && !(state is FavoritesState.Content)) {
            adapter.clearList()
        }
    }


    private fun showPlayer(track: Track) {

        findNavController().navigate(
            R.id.action_mediatecFragment_to_playerActivity,
            PlayerActivity.createArgs(track)
        )
    }

    private fun clickListener(track: Track) {

        if (track.previewUrl.isEmpty()) {
            Toast.makeText(
                requireContext(),
                resources.getText(R.string.error_empty_url),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            viewModel.showPlayer(track)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.fillData()
    }
}