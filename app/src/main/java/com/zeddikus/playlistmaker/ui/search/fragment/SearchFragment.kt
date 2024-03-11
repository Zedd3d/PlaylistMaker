package com.zeddikus.playlistmaker.ui.search.fragment

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.zeddikus.playlistmaker.R
import com.zeddikus.playlistmaker.databinding.FragmentSearchBinding
import com.zeddikus.playlistmaker.domain.search.model.Track
import com.zeddikus.playlistmaker.domain.search.model.TrackRepositoryState
import com.zeddikus.playlistmaker.ui.main.activity.MainActivity
import com.zeddikus.playlistmaker.ui.player.fragment.PlayerFragment
import com.zeddikus.playlistmaker.ui.search.track.TracksAdapter
import com.zeddikus.playlistmaker.ui.search.view_model.SearchFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: TracksAdapter
    private lateinit var historyAdapter: TracksAdapter
    private val viewModel by viewModel<SearchFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        activity?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        )
        binding = FragmentSearchBinding.inflate(layoutInflater)
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

        binding.recyclerTracksHistory.layoutManager = LinearLayoutManager(requireContext())
        historyAdapter = TracksAdapter(listOf<Track>()) { track: Track ->
            clickListener(
                track
            )
        }
        binding.recyclerTracksHistory.adapter = historyAdapter
        setListenersWatchersObservers()
    }

    private fun initalizePlaceholder() {
        binding.placeholderTrouble.placeholderTroubleText.text = getText(R.string.favorites_empty)
        binding.placeholderTrouble.placeholderTroubleButton.visibility = View.VISIBLE
        binding.placeholderTrouble.root.visibility = View.GONE
    }

    private fun setListenersWatchersObservers() {

        binding.btnSearchClear.setOnClickListener {
            clearSearchText(binding.btnSearchClear, binding.vTextSearch)
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        binding.placeholderTrouble.placeholderTroubleButton.setOnClickListener {
            searchDebounce()
        }

        binding.vTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchDebounce()
            }
            false
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkClearButtonVisibility(s)
                searchDebounce()
                if (binding.vTextSearch.hasFocus() && s?.isEmpty() == true) {
                    viewModel.showHistory()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.vTextSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewModel.showHistory()
            }
        }

        binding.vTextSearch.addTextChangedListener(simpleTextWatcher)

        viewModel.getState().observe(viewLifecycleOwner) { state ->
            if (when (state) {
                    is TrackRepositoryState.ShowHistory -> {
                        historyAdapter.setNewList(state.trackList)
                        state.showAdapter
                    }

                    else -> true
                }
            ) showListState(state)

        }

        viewModel.getShowPlayerTrigger().observe(viewLifecycleOwner) { track -> showPlayer(track) }
    }


    private fun searchDebounce() {
        search()
    }

    private fun search() {
        val filter = binding.vTextSearch.text.toString()
        val config = Resources.getSystem().configuration
        val locale: String = config.locales.get(0)?.language ?: "en_EN"
        viewModel.search(filter, locale)
    }

    private fun showListState(state: TrackRepositoryState) {

        binding.placeholderTrouble.placeholderTrouble.visibility = when (state) {
            is TrackRepositoryState.ErrorNetwork -> {
                Glide.with(this).load(R.drawable.ic_network_trouble).dontTransform()
                    .into(binding.placeholderTrouble.placeholderTroubleCenterImage)
                binding.placeholderTrouble.placeholderTroubleText.text =
                    resources.getText(R.string.error_network_trouble)
                View.VISIBLE
            }

            is TrackRepositoryState.ErrorEmpty -> {
                Glide.with(this).load(R.drawable.ic_sad_smile).dontTransform()
                    .into(binding.placeholderTrouble.placeholderTroubleCenterImage)
                binding.placeholderTrouble.placeholderTroubleText.text =
                    resources.getText(R.string.error_track_list_is_empty)
                View.VISIBLE
            }

            else -> View.GONE
        }
        binding.recyclerTracks.visibility = when (state) {
            is TrackRepositoryState.ShowListResult -> {
                adapter.setNewList(state.trackList)
                View.VISIBLE
            }

            else -> {
                View.GONE
            }
        }

        binding.linearTracksHistory.visibility = when (state) {
            is TrackRepositoryState.ShowHistory -> {
                historyAdapter.setNewList(state.trackList)
                if (historyAdapter.itemCount == 0) View.GONE else View.VISIBLE
            }

            else -> {
                View.GONE
            }
        }

        binding.progressBarSearchTracks.visibility = when (state) {
            is TrackRepositoryState.SearchInProgress -> View.VISIBLE
            else -> View.GONE
        }
        binding.placeholderTrouble.placeholderTroubleButton.visibility = when (state) {
            is TrackRepositoryState.ErrorNetwork -> View.VISIBLE
            else -> View.GONE
        }

        if (binding.recyclerTracks.visibility == View.GONE && !(state is TrackRepositoryState.ShowHistory)) {
            adapter.clearList()
        }
    }

    private fun checkClearButtonVisibility(s: CharSequence?) {
        if (s.isNullOrEmpty()) {
            binding.btnSearchClear.visibility = View.GONE
            viewModel.showHistory()
        }else {
            binding.btnSearchClear.visibility =  View.VISIBLE
        }
    }

    private fun clearSearchText(btnClose: ImageView, editField: EditText) {
        editField.text.clear()
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(editField.windowToken, 0)
        btnClose.visibility = View.GONE
        search()
    }

    private fun showPlayer(track: Track) {

        findNavController().navigate(
            R.id.action_searchFragment_to_playerFragment,
            PlayerFragment.createArgs(track)
        )

    }

    private fun clickListener(track: Track) {

        viewModel.addTrackToHistory(track)

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

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).animateBottomNavigationView(View.VISIBLE)
    }

}