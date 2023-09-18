package com.zeddikus.playlistmaker


import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }

    lateinit var vTextSearch: EditText
    lateinit var recyclerTracks: RecyclerView
    lateinit var recyclerHistory: RecyclerView
    lateinit var linearHistory: LinearLayout
    lateinit var vTroublePlaceholder: LinearLayout
    lateinit var vTroublePlaceholderText: TextView
    lateinit var vTroublePlaceholderButton: Button
    lateinit var clearButton: ImageView
    lateinit var placeholderTroubleCenterImage: ImageView
    lateinit var progressBarSearch: ProgressBar
    lateinit var adapter: TracksAdapter
    lateinit var historyAdapter: TracksAdapter
    lateinit var clearHistoryButton: Button
    lateinit var searchHistoryHandler: SearchHistoryHandler
    lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener
    var lastState = TrackListState.GONE

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesAPI::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        lateinitInitalize()

        val app = (applicationContext as App)
        searchHistoryHandler = SearchHistoryHandler(app)

        val btnBack = findViewById<ImageButton>(R.id.imgBtnBack)
        btnBack.setOnClickListener { finish() }
        clearButton.setOnClickListener { clearSearchText(clearButton, vTextSearch) }
        clearHistoryButton.setOnClickListener {
            searchHistoryHandler.clearHistory()
            showHistory()
        }
        vTroublePlaceholderButton.setOnClickListener { search() }

        recyclerTracks.layoutManager = LinearLayoutManager(this)
        adapter = TracksAdapter(listOf<Track>(),searchHistoryHandler)

        recyclerTracks.adapter = adapter

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkClearButtonVisibility(s)
                if (vTextSearch.hasFocus() && s?.isEmpty() == true) {
                    showHistory()
                } else {
                    showListState(lastState)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        vTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }
        vTextSearch.addTextChangedListener(simpleTextWatcher)

        recyclerHistory.layoutManager = LinearLayoutManager(this)
        historyAdapter = TracksAdapter(listOf<Track>(),null)
        recyclerHistory.adapter = historyAdapter
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

    fun lateinitInitalize(){
        clearHistoryButton = findViewById<Button>(R.id.clearHistoryButton)
        vTextSearch = findViewById<EditText>(R.id.vTextSearch)
        recyclerTracks = findViewById<RecyclerView>(R.id.recyclerTracks)
        linearHistory = findViewById<LinearLayout>(R.id.linearTracksHistory)
        recyclerHistory = findViewById<RecyclerView>(R.id.recyclerTracksHistory)
        vTroublePlaceholder = findViewById<LinearLayout>(R.id.placeholderTrouble)
        vTroublePlaceholderText = findViewById<TextView>(R.id.placeholderTroubleText)
        vTroublePlaceholderButton = findViewById<Button>(R.id.placeholderTroubleButton)
        placeholderTroubleCenterImage = findViewById<ImageView>(R.id.placeholderTroubleCenterImage)
        clearButton = findViewById<ImageView>(R.id.btnSearchClear)
        progressBarSearch = findViewById<ProgressBar>(R.id.progressBarSearchTracks)


    }
    private fun showHistory() {
        historyAdapter.setNewList(searchHistoryHandler.getHistory())
        if (historyAdapter.itemCount>0){
            showListState(TrackListState.SHOW_HISTORY)
        } else {
            showListState(TrackListState.GONE)
        }
    }

    private fun search(){
        showListState(TrackListState.SEARCH_IN_PROGRESS)
        val filter = vTextSearch.text.toString()
        if (filter.isNotEmpty()) {
            itunesService.findTracks(filter).enqueue(object : Callback<TracksResponse> {
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    if (response.code() == 200) {
                        if (response.body()?.results?.isNotEmpty() == true) {
                            showListState(TrackListState.OK)

                            adapter.setNewList(response.body()?.results!!.toList())

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

        vTroublePlaceholder.visibility = when (state) {
            TrackListState.ERROR_NETWORK -> View.VISIBLE
            TrackListState.ERROR_EMPTY -> View.VISIBLE
            else -> View.GONE
        }
        recyclerTracks.visibility = when (state) {
            TrackListState.OK -> View.VISIBLE
            else -> {View.GONE}
        }

        linearHistory.visibility = when (state) {
            TrackListState.SHOW_HISTORY ->  View.VISIBLE
            else -> {View.GONE}
        }

        progressBarSearch.visibility = when (state) {
            TrackListState.SEARCH_IN_PROGRESS -> View.VISIBLE
            else -> View.GONE
        }
        vTroublePlaceholderButton.visibility = when (state) {
            TrackListState.ERROR_NETWORK -> View.VISIBLE
            else -> View.GONE
        }

        if (state == TrackListState.ERROR_NETWORK){
            Glide.with(this).load(R.drawable.ic_network_trouble).dontTransform().into(placeholderTroubleCenterImage)
            vTroublePlaceholderText.setText(resources.getText(R.string.error_network_trouble))
        } else if (state == TrackListState.ERROR_EMPTY){
            Glide.with(this).load(R.drawable.ic_sad_smile).dontTransform().into(placeholderTroubleCenterImage)
            vTroublePlaceholderText.setText(resources.getText(R.string.error_track_list_is_empty))
        }

        if (recyclerTracks.visibility == View.GONE && !(state == TrackListState.SHOW_HISTORY) ){
            adapter.clearList()
        }
    }

    fun isPortraitOrientation(): Boolean {
        return resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    fun updateViewParameters() {
        val params: LinearLayout.LayoutParams =
            vTroublePlaceholder.layoutParams as LinearLayout.LayoutParams
        params.setMargins(0, General.dpToPx((if (isPortraitOrientation()) 102f else 0f), this),0,0)
        vTroublePlaceholder.setLayoutParams(params)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT,vTextSearch.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        vTextSearch.setText(savedInstanceState.getString(SEARCH_TEXT))
        updateViewParameters()
    }

    private fun checkClearButtonVisibility(s: CharSequence?) {
        if (s.isNullOrEmpty()) {
            clearButton.visibility = View.GONE
            showHistory()
        }else {
            clearButton.visibility =  View.VISIBLE
        }
    }

    private fun clearSearchText(btnClose: ImageView,editField: EditText) {
        editField.text.clear()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(editField.windowToken, 0)
        btnClose.visibility = View.GONE
        search()
    }
}


