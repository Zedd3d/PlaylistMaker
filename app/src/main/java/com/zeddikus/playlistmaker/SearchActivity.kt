package com.zeddikus.playlistmaker


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
import com.google.gson.Gson
import com.zeddikus.playlistmaker.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    private companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DELAY = 1500L
        private val itunesBaseUrl = "https://itunes.apple.com"
        private val mainHandler = Handler(Looper.getMainLooper())

    }

    lateinit var binding: ActivitySearchBinding
    lateinit var adapter: TracksAdapter
    lateinit var historyAdapter: TracksAdapter
    lateinit var searchHistoryHandler: SearchHistoryHandler
    lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener
    lateinit var searchRunnable: Runnable
    var trackIsStarted = false
    var lastState = TrackListState.GONE

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesAPI::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)

        val app = (applicationContext as App)
        searchHistoryHandler = SearchHistoryHandler(app)

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
            searchHistoryHandler.clearHistory()
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
        adapter = TracksAdapter(listOf<Track>(),searchHistoryHandler)
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
        historyAdapter.setNewList(searchHistoryHandler.getHistory())

        listener = SharedPreferences.OnSharedPreferenceChangeListener() { sharedPreferences, key ->
                if (key == searchHistoryHandler.SP_SEARCH_HISTORY) {
                    historyAdapter.setNewList(searchHistoryHandler.getHistory())
                }
            }
        searchHistoryHandler.sharedPref.registerOnSharedPreferenceChangeListener(listener)

        showHistory()
        updateViewParameters()

    }

    private fun showHistory() {
        historyAdapter.setNewList(searchHistoryHandler.getHistory())
        if (historyAdapter.itemCount>0){
            showListState(TrackListState.SHOW_HISTORY)
        } else {
            showListState(TrackListState.GONE)
        }
    }

    private fun searchDebounce(){
        showListState(TrackListState.SEARCH_IN_PROGRESS)
        mainHandler.removeCallbacks(searchRunnable)
        mainHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
    private fun search(){
        showListState(TrackListState.SEARCH_IN_PROGRESS)
        val filter = binding.vTextSearch.text.toString()
        val config = Resources.getSystem().configuration
        val locale: String = config.locales.get(0)?.language ?: "en_EN"
        if (filter.isNotEmpty()) {
            itunesService.findTracks(filter, locale).enqueue(object : Callback<TracksResponse> {
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    if (response.code() == 200) {
                        if (response.body()?.results?.isNotEmpty() == true) {
                            showListState(TrackListState.OK)
                            adapter.setNewList(response.body()?.results ?:mutableListOf<Track>() .toList())

                        } else {showListState(TrackListState.ERROR_EMPTY)}
                    } else {
                        showListState(TrackListState.ERROR_NETWORK)
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    showListState(TrackListState.ERROR_NETWORK)
                }
            })
        }else{
            showHistory()
        }
    }

    private fun showListState(state: TrackListState) {

        lastState = if (state == TrackListState.SHOW_HISTORY || state == TrackListState.SEARCH_IN_PROGRESS ) lastState else state

        binding.placeholderTrouble.visibility = when (state) {
            TrackListState.ERROR_NETWORK -> View.VISIBLE
            TrackListState.ERROR_EMPTY -> View.VISIBLE
            else -> View.GONE
        }
        binding.recyclerTracks.visibility = when (state) {
            TrackListState.OK -> View.VISIBLE
            else -> {View.GONE}
        }

        binding.linearTracksHistory.visibility = when (state) {
            TrackListState.SHOW_HISTORY ->  View.VISIBLE
            else -> {View.GONE}
        }

        binding.progressBarSearchTracks.visibility = when (state) {
            TrackListState.SEARCH_IN_PROGRESS -> View.VISIBLE
            else -> View.GONE
        }
        binding.placeholderTroubleButton.visibility = when (state) {
            TrackListState.ERROR_NETWORK -> View.VISIBLE
            else -> View.GONE
        }

        if (state == TrackListState.ERROR_NETWORK){
            Glide.with(this).load(R.drawable.ic_network_trouble).dontTransform().into(binding.placeholderTroubleCenterImage)
            binding.placeholderTroubleText.setText(resources.getText(R.string.error_network_trouble))
        } else if (state == TrackListState.ERROR_EMPTY){
            Glide.with(this).load(R.drawable.ic_sad_smile).dontTransform().into(binding.placeholderTroubleCenterImage)
            binding.placeholderTroubleText.setText(resources.getText(R.string.error_track_list_is_empty))
        }

        if (binding.recyclerTracks.visibility == View.GONE && !(state == TrackListState.SHOW_HISTORY) ){
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
        if (trackIsStarted){
            //Toast.makeText(this,"Немного подождите, трек недавно был запущен",Toast.LENGTH_SHORT).show()
            return
        }

        trackIsStarted = true
        mainHandler.postDelayed(
            object : Runnable {
                override fun run() {
                    trackIsStarted = false
                }
            }
            , CLICK_DELAY
        )

        val intent = Intent(this,PlayerActivity::class.java)
        intent.putExtra("Track",Gson().toJson(track))
        startActivity(intent)
    }
}


