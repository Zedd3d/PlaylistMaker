package com.zeddikus.playlistmaker


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class SearchActivity : AppCompatActivity() {



    private companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }

    lateinit var vTextSearch:EditText
    lateinit var trackList_Data:ArrayList<Track>
    lateinit var adapter: TracksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        vTextSearch = findViewById<EditText>(R.id.vTextSearch)
        fillTrackList()
        val clearButton = findViewById<ImageView>(R.id.btnSearchClear)
        clearButton.setOnClickListener{clearSearchText(clearButton,vTextSearch)}
        val btnBack = findViewById<ImageButton>(R.id.imgBtnBack)
        btnBack.setOnClickListener{finish()}



        val recyclerTracks = findViewById<RecyclerView>(R.id.recyclerTracks)
        recyclerTracks.layoutManager = LinearLayoutManager(this)

        adapter = TracksAdapter(trackList_Data.filter { true })

        recyclerTracks.adapter = adapter

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                setFilter(vTextSearch.text.toString())
                adapter.notifyDataSetChanged()
            }
        }

        vTextSearch.addTextChangedListener(simpleTextWatcher)

    }

    fun setFilter(filter:String) {

        if (filter == ""){
            adapter.setNewList(trackList_Data.filter {false})
        } else {
            val pattern = filter.lowercase().toRegex()
            adapter.setNewList(trackList_Data.filter { pattern.containsMatchIn(it.trackName.lowercase()) or pattern.containsMatchIn(it.artistName.lowercase()) } )
        }
    }




    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT,vTextSearch.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        vTextSearch.setText(savedInstanceState.getString(SEARCH_TEXT))
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
    private fun clearSearchText(btnClose: ImageView,editField: EditText) {
        editField.text.clear()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(editField.windowToken, 0)
        btnClose.visibility = View.GONE

    }

    private fun fillTrackList() {

        trackList_Data = ArrayList<Track>()
        trackList_Data.add(Track(
            "Smells Like Teen Spirit",
            "Nirvana",
            "5:01",
            "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
        ))
        trackList_Data.add(Track(
            "Billie Jean",
            "Michael Jackson",
            "4:35",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
        ))
        trackList_Data.add(Track(
            "Stayin ' Alive",
            "Bee Gees",
            "4:10",
            "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
        ))
        trackList_Data.add(Track(
            "Whole Lotta Love",
            "Led Zeppelin",
            "5:33",
            "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
        ))
        trackList_Data.add(Track(
            "Sweet Child O 'Mine",
            "Guns N' Roses",
            "5:03",
            "https ://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
        ))

        trackList_Data.add(Track(
            "Тестовое очень длинное название трека, прям очень длинное для тестов, если недостаточно длинное, то ещё пара слов",
            "Длинное имя исполнителя, прям очень длинное для тестов, если недостаточно длинное, то ещё пара слов",
            "5:03",
            "https://cdn-icons-png.flaticon.com/512/83/83326.png"
        ))

    }

}


