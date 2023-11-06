package com.zeddikus.playlistmaker.presentation.ui


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.zeddikus.playlistmaker.Creator
import com.zeddikus.playlistmaker.utils.General
import com.zeddikus.playlistmaker.R
import com.zeddikus.playlistmaker.databinding.ActivitySearchBinding
import com.zeddikus.playlistmaker.domain.api.TracksInteractor
import com.zeddikus.playlistmaker.domain.models.Track
import com.zeddikus.playlistmaker.data.dto.TrackSearchResult
import com.zeddikus.playlistmaker.domain.api.SearchHistoryInteractor
import com.zeddikus.playlistmaker.domain.models.PlayerState
import com.zeddikus.playlistmaker.domain.models.TrackRepositoryState
import com.zeddikus.playlistmaker.presentation.track.TracksAdapter


class SearchActivity : AppCompatActivity(), TracksInteractor.TracksConsumer{

    private companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DELAY = 1500L
        private val mainHandler = Handler(Looper.getMainLooper())
    }

    lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: TracksAdapter
    private lateinit var historyAdapter: TracksAdapter
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener
    private lateinit var searchRunnable: Runnable
    private var mediaPlayerStarting = false
    private var currentState = TrackRepositoryState.GONE
    private val jsonHandler = Creator.provideJsonHandler()
    private val tracksInteractor = Creator.provideTracksInteractor()
    private val sharedPrefHandler = Creator.provideSharedPreferencesHandler()
    private var runnableConsumer: Runnable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)

        searchHistoryInteractor = Creator.provideSearchHistoryInteractor()

        searchRunnable = Runnable {
            search()
        }

        binding.imgBtnBack.setOnClickListener {
            finish()
        }

        binding.btnSearchClear.setOnClickListener {
            clearSearchText(binding.btnSearchClear, binding.vTextSearch)
        }

        binding.clearHistoryButton.setOnClickListener {
            searchHistoryInteractor.clearHistory()
            showHistory()
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

        binding.recyclerTracks.layoutManager = LinearLayoutManager(this)
        adapter = TracksAdapter(listOf<Track>(),searchHistoryInteractor)
        binding.recyclerTracks.adapter = adapter

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkClearButtonVisibility(s)
                searchDebounce()
                if (binding.vTextSearch.hasFocus() && s?.isEmpty() == true) {
                    mainHandler.removeCallbacks(searchRunnable)
                    showHistory()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.vTextSearch.addTextChangedListener(simpleTextWatcher)

        binding.recyclerTracksHistory.layoutManager = LinearLayoutManager(this)
        historyAdapter = TracksAdapter(listOf<Track>(),null)
        binding.recyclerTracksHistory.adapter = historyAdapter
        historyAdapter.setNewList(searchHistoryInteractor.getHistory())

        listener = SharedPreferences.OnSharedPreferenceChangeListener() { sharedPreferences, key ->
                if (key == searchHistoryInteractor.SP_SEARCH_HISTORY) {
                    historyAdapter.setNewList(searchHistoryInteractor.getHistory())
                }
            }
        sharedPrefHandler.setSharedPreferencesChangeListener(listener)

        showHistory()
        updateViewParameters()
    }

    private fun showHistory() {
        historyAdapter.setNewList(searchHistoryInteractor.getHistory())
        if (historyAdapter.itemCount>0){
            showListState(TrackRepositoryState.SHOW_HISTORY)
        } else {
            showListState(TrackRepositoryState.GONE)
        }
    }

    private fun searchDebounce(){
        showListState(TrackRepositoryState.SEARCH_IN_PROGRESS)
        mainHandler.removeCallbacks(searchRunnable)
        mainHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
    private fun search(){
        showListState(TrackRepositoryState.SEARCH_IN_PROGRESS)
        val filter = binding.vTextSearch.text.toString()
        val config = Resources.getSystem().configuration
        val locale: String = config.locales.get(0)?.language ?: "en_EN"
        if (filter.isNotEmpty()) {
            tracksInteractor.searchTracks(filter,locale, this)
        }else{
            showHistory()
        }
    }

    private fun showListState(state: TrackRepositoryState) {

        currentState = if (state == TrackRepositoryState.SHOW_HISTORY || state == TrackRepositoryState.SEARCH_IN_PROGRESS ) currentState else state

        binding.placeholderTrouble.visibility = when (state) {
            TrackRepositoryState.ERROR_NETWORK -> View.VISIBLE
            TrackRepositoryState.ERROR_EMPTY -> View.VISIBLE
            else -> View.GONE
        }
        binding.recyclerTracks.visibility = when (state) {
            TrackRepositoryState.OK -> View.VISIBLE
            else -> {View.GONE}
        }

        binding.linearTracksHistory.visibility = when (state) {
            TrackRepositoryState.SHOW_HISTORY ->  View.VISIBLE
            else -> {View.GONE}
        }

        binding.progressBarSearchTracks.visibility = when (state) {
            TrackRepositoryState.SEARCH_IN_PROGRESS -> View.VISIBLE
            else -> View.GONE
        }
        binding.placeholderTroubleButton.visibility = when (state) {
            TrackRepositoryState.ERROR_NETWORK -> View.VISIBLE
            else -> View.GONE
        }

        if (state == TrackRepositoryState.ERROR_NETWORK){
            Glide.with(this).load(R.drawable.ic_network_trouble).dontTransform().into(binding.placeholderTroubleCenterImage)
            binding.placeholderTroubleText.setText(resources.getText(R.string.error_network_trouble))
        } else if (state == TrackRepositoryState.ERROR_EMPTY){
            Glide.with(this).load(R.drawable.ic_sad_smile).dontTransform().into(binding.placeholderTroubleCenterImage)
            binding.placeholderTroubleText.setText(resources.getText(R.string.error_track_list_is_empty))
        }

        if (binding.recyclerTracks.visibility == View.GONE && !(state == TrackRepositoryState.SHOW_HISTORY) ){
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT,binding.vTextSearch.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.vTextSearch.setText(savedInstanceState.getString(SEARCH_TEXT))
        updateViewParameters()
    }

    private fun checkClearButtonVisibility(s: CharSequence?) {
        if (s.isNullOrEmpty()) {
            binding.btnSearchClear.visibility = View.GONE
            showHistory()
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
        intent.putExtra("Track",jsonHandler.toJson(track))
        startActivity(intent)
    }
    override fun consume(trackSearchResult: TrackSearchResult) {
        runnableConsumer?.let {
                prevRunnable -> mainHandler.removeCallbacks(prevRunnable)
        }

        val newRunnable = Runnable{
            adapter.setNewList(trackSearchResult.listTracks)
            showListState(trackSearchResult.state)
        }

        runnableConsumer = newRunnable

        mainHandler.post(newRunnable)
    }
}


