package com.zeddikus.playlistmaker


import android.content.Context
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
import android.widget.ImageView.ScaleType
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
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
    lateinit var recyclerTracks : RecyclerView
    lateinit var vTroublePlaceholder : LinearLayout
    lateinit var vTroublePlaceholderText : TextView
    lateinit var vTroublePlaceholderButton : Button
    lateinit var clearButton : ImageView
    lateinit var placeholderTroubleCenterImage : ImageView
    lateinit var progressBarSearch : ProgressBar
    lateinit var adapter: TracksAdapter

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesAPI::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        vTextSearch = findViewById<EditText>(R.id.vTextSearch)
        recyclerTracks = findViewById<RecyclerView>(R.id.recyclerTracks)
        vTroublePlaceholder = findViewById<LinearLayout>(R.id.placeholderTrouble)
        vTroublePlaceholderText = findViewById<TextView>(R.id.placeholderTroubleText)
        vTroublePlaceholderButton = findViewById<Button>(R.id.placeholderTroubleButton)
        placeholderTroubleCenterImage = findViewById<ImageView>(R.id.placeholderTroubleCenterImage)
        clearButton = findViewById<ImageView>(R.id.btnSearchClear)
        progressBarSearch = findViewById<ProgressBar>(R.id.progressBarSearchTracks)
        val btnBack = findViewById<ImageButton>(R.id.imgBtnBack)


        clearButton.setOnClickListener{clearSearchText(clearButton,vTextSearch)}
        btnBack.setOnClickListener{finish()}
        vTroublePlaceholderButton.setOnClickListener{search()}

        recyclerTracks.layoutManager = LinearLayoutManager(this)
        adapter = TracksAdapter(listOf<Track>())
        recyclerTracks.adapter = adapter

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkClearButtonVisibility(s)}
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
        showListState(TrackListState.GONE)
        updateViewParameters()
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
            showListState(TrackListState.GONE)
        }
    }

    private fun showListState(state: TrackListState) {

        vTroublePlaceholder.visibility = state.getPlaceholderVisible()
        recyclerTracks.visibility = state.getTrackListVisible()
        progressBarSearch.visibility = state.getProgressBarVisible()
        vTroublePlaceholderButton.visibility = state.getUpdateButtonVisible()

        if (state == TrackListState.ERROR_NETWORK){
            Glide.with(this).load(R.drawable.ic_network_trouble).dontTransform().into(placeholderTroubleCenterImage)
            vTroublePlaceholderText.setText(resources.getText(R.string.error_network_trouble))
        } else if (state == TrackListState.ERROR_EMPTY){
            Glide.with(this).load(R.drawable.ic_sad_smile).dontTransform().into(placeholderTroubleCenterImage)
            vTroublePlaceholderText.setText(resources.getText(R.string.error_track_list_is_empty))
        }

        if (recyclerTracks.visibility == View.GONE){
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
            showListState(TrackListState.GONE)
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


