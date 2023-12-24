package com.zeddikus.playlistmaker.ui.search.activity


import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.zeddikus.playlistmaker.R
import com.zeddikus.playlistmaker.databinding.ActivitySearchBinding
import com.zeddikus.playlistmaker.databinding.PlaceholderEmptyErrorBinding
import com.zeddikus.playlistmaker.domain.search.model.TrackRepositoryState
import com.zeddikus.playlistmaker.domain.sharing.model.Track
import com.zeddikus.playlistmaker.ui.player.activity.PlayerActivity
import com.zeddikus.playlistmaker.ui.search.track.TracksAdapter
import com.zeddikus.playlistmaker.ui.search.view_model.SearchActivityViewModel
import org.koin.android.ext.android.inject


class SearchActivity : AppCompatActivity() {

    lateinit var binding: ActivitySearchBinding
    lateinit var placeholderBinding: PlaceholderEmptyErrorBinding
    private lateinit var adapter: TracksAdapter
    private lateinit var historyAdapter: TracksAdapter
    private lateinit var searchRunnable: Runnable

    private val viewModel: SearchActivityViewModel by inject()

    private companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val mainHandler = Handler(Looper.getMainLooper())
        private const val TRACK_DATA = "TrackData"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        placeholderBinding = PlaceholderEmptyErrorBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)
        inflatePlaceholder()
        searchRunnable = Runnable {
            search()
        }

        binding.recyclerTracks.layoutManager = LinearLayoutManager(this)
        adapter = TracksAdapter(listOf<Track>()) { track: Track ->
            clickListener(
                track
            )
        }
        binding.recyclerTracks.adapter = adapter

        binding.recyclerTracksHistory.layoutManager = LinearLayoutManager(this)
        historyAdapter = TracksAdapter(listOf<Track>()) { track: Track ->
            clickListener(
                track
            )
        }
        binding.recyclerTracksHistory.adapter = historyAdapter
        setListenersWatchersObservers()


    }

    private fun inflatePlaceholder() {

        placeholderBinding.placeholderTroubleText.text = getText(R.string.favorites_empty)
        binding.mainLL.addView(placeholderBinding.root)
        placeholderBinding.placeholderTroubleButton.visibility = View.VISIBLE
        placeholderBinding.root.visibility = View.GONE
    }

    private fun setListenersWatchersObservers() {
        binding.imgBtnBack.setOnClickListener {
            finish()
        }

        binding.btnSearchClear.setOnClickListener {
            clearSearchText(binding.btnSearchClear, binding.vTextSearch)
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        placeholderBinding.placeholderTroubleButton.setOnClickListener {
            searchRunnable.run()
        }

        binding.vTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchRunnable.run()
            }
            false
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkClearButtonVisibility(s)
                searchDebounce()
                if (binding.vTextSearch.hasFocus() && s?.isEmpty() == true) {
                    mainHandler.removeCallbacks(searchRunnable)
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

        viewModel.getState().observe(this) { state ->
            if (when (state) {
                    is TrackRepositoryState.showHistory -> {
                        historyAdapter.setNewList(state.trackList)
                        state.showAdapter
                    }

                    else -> true
                }
            ) showListState(state)

        }

        viewModel.getshowPlayerTrigger().observe(this) { track -> showPlayer(track) }
    }


    private fun searchDebounce() {
        showListState(TrackRepositoryState.searchInProgress)
        mainHandler.removeCallbacks(searchRunnable)
        mainHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun search() {
        val filter = binding.vTextSearch.text.toString()
        val config = Resources.getSystem().configuration
        val locale: String = config.locales.get(0)?.language ?: "en_EN"
        viewModel.search(filter, locale)
    }

    private fun showListState(state: TrackRepositoryState) {

        placeholderBinding.placeholderTrouble.visibility = when (state) {
            is TrackRepositoryState.errorNetwork -> {
                Glide.with(this).load(R.drawable.ic_network_trouble).dontTransform()
                    .into(placeholderBinding.placeholderTroubleCenterImage)
                placeholderBinding.placeholderTroubleText.text =
                    resources.getText(R.string.error_network_trouble)
                View.VISIBLE
            }

            is TrackRepositoryState.errorEmpty -> {
                Glide.with(this).load(R.drawable.ic_sad_smile).dontTransform()
                    .into(placeholderBinding.placeholderTroubleCenterImage)
                placeholderBinding.placeholderTroubleText.text =
                    resources.getText(R.string.error_track_list_is_empty)
                View.VISIBLE
            }

            else -> View.GONE
        }
        binding.recyclerTracks.visibility = when (state) {
            is TrackRepositoryState.showListResult -> {
                adapter.setNewList(state.trackList)
                View.VISIBLE
            }

            else -> {
                View.GONE
            }
        }

        binding.linearTracksHistory.visibility = when (state) {
            is TrackRepositoryState.showHistory -> {
                historyAdapter.setNewList(state.trackList)
                if (historyAdapter.itemCount == 0) View.GONE else View.VISIBLE
            }

            else -> {
                View.GONE
            }
        }

        binding.progressBarSearchTracks.visibility = when (state) {
            is TrackRepositoryState.searchInProgress -> View.VISIBLE
            else -> View.GONE
        }
        placeholderBinding.placeholderTroubleButton.visibility = when (state) {
            is TrackRepositoryState.errorNetwork -> View.VISIBLE
            else -> View.GONE
        }

        if (binding.recyclerTracks.visibility == View.GONE && !(state is TrackRepositoryState.showHistory)) {
            adapter.clearList()
        }
    }

    private fun isPortraitOrientation(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
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
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(editField.windowToken, 0)
        btnClose.visibility = View.GONE
        search()
    }

    private fun showPlayer(track: Track) {

        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(TRACK_DATA, track)
        startActivity(intent)
    }

    private fun clickListener(track: Track) {

        viewModel.addTrackToHistory(track)

        if (track.previewUrl.isEmpty()) {
            Toast.makeText(
                this,
                resources.getText(R.string.error_empty_url),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            viewModel.showPlayer(track)
        }
    }

}


