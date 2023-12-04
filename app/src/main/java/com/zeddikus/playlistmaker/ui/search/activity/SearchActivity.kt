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
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.zeddikus.playlistmaker.R
import com.zeddikus.playlistmaker.databinding.ActivitySearchBinding
import com.zeddikus.playlistmaker.domain.search.model.TrackRepositoryState
import com.zeddikus.playlistmaker.domain.sharing.model.Track
import com.zeddikus.playlistmaker.ui.player.activity.PlayerActivity
import com.zeddikus.playlistmaker.ui.search.track.TracksAdapter
import com.zeddikus.playlistmaker.ui.search.view_model.SearchActivityViewModel
import com.zeddikus.playlistmaker.utils.General


class SearchActivity : AppCompatActivity() {

    lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: TracksAdapter
    private lateinit var historyAdapter: TracksAdapter
    private lateinit var searchRunnable: Runnable
    private var mediaPlayerStarting = false

    private val viewModel: SearchActivityViewModel by lazy {
        ViewModelProvider(this)[SearchActivityViewModel::class.java]
    }

    private companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DELAY = 1500L
        private val mainHandler = Handler(Looper.getMainLooper())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)

        searchRunnable = Runnable {
            search()
        }

        binding.recyclerTracks.layoutManager = LinearLayoutManager(this)
        adapter = TracksAdapter(listOf<Track>(), false, viewModel)
        binding.recyclerTracks.adapter = adapter

        binding.recyclerTracksHistory.layoutManager = LinearLayoutManager(this)
        historyAdapter = TracksAdapter(listOf<Track>(), true, viewModel)
        binding.recyclerTracksHistory.adapter = historyAdapter

        setListenersWatchersObservers()

        updateViewParameters()
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

        binding.placeholderTroubleButton.setOnClickListener {
            searchRunnable.run()
        }

        binding.vTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchRunnable.run()
                true
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

        binding.placeholderTrouble.visibility = when (state) {
            is TrackRepositoryState.errorNetwork -> {
                Glide.with(this).load(R.drawable.ic_network_trouble).dontTransform()
                    .into(binding.placeholderTroubleCenterImage)
                binding.placeholderTroubleText.setText(resources.getText(R.string.error_network_trouble))
                View.VISIBLE
            }

            is TrackRepositoryState.errorEmpty -> {
                Glide.with(this).load(R.drawable.ic_sad_smile).dontTransform()
                    .into(binding.placeholderTroubleCenterImage)
                binding.placeholderTroubleText.setText(resources.getText(R.string.error_track_list_is_empty))
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
        binding.placeholderTroubleButton.visibility = when (state) {
            is TrackRepositoryState.errorNetwork -> View.VISIBLE
            else -> View.GONE
        }

        if (binding.recyclerTracks.visibility == View.GONE && !(state is TrackRepositoryState.showHistory)) {
            adapter.clearList()
        }
    }

    fun isPortraitOrientation(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    fun updateViewParameters() {
        val params: LinearLayout.LayoutParams =
            binding.placeholderTrouble.layoutParams as LinearLayout.LayoutParams
        params.setMargins(0, General.dpToPx((if (isPortraitOrientation()) 102f else 0f), this),0,0)
        binding.placeholderTrouble.setLayoutParams(params)
    }

    private fun checkClearButtonVisibility(s: CharSequence?) {
        if (s.isNullOrEmpty()) {
            binding.btnSearchClear.visibility = View.GONE
            viewModel.showHistory()
        }else {
            binding.btnSearchClear.visibility =  View.VISIBLE
        }
    }

    private fun clearSearchText(btnClose: ImageView,editField: EditText) {
        editField.text.clear()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(editField.windowToken, 0)
        btnClose.visibility = View.GONE
        search()
    }

    fun showPlayer(track: Track) {
        if (mediaPlayerStarting){
            //Toast.makeText(this,"Немного подождите, трек недавно был запущен",Toast.LENGTH_SHORT).show()
            return
        }

        mediaPlayerStarting = true
        mainHandler.postDelayed(
            object : Runnable {
                override fun run() {
                    mediaPlayerStarting = false
                }
            }
            , CLICK_DELAY
        )

        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("Track", Gson().toJson(track))
        startActivity(intent)
    }

}


