package com.zeddikus.playlistmaker

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    lateinit var titleText:TextView
    lateinit var bandText:TextView
    lateinit var trackTime:TextView
    lateinit var albumName:TextView
    lateinit var yearValue:TextView
    lateinit var genreValue:TextView
    lateinit var countryValue:TextView
    lateinit var currentTrackTime:TextView
    lateinit var backButton:ImageButton
    lateinit var track: Track
    lateinit var coverImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        initalizeViews()

        track = Gson().fromJson<Track>(intent.getStringExtra("Track"), object: TypeToken<Track>(){}.type)
        titleText.text = track.trackName
        bandText.text = track.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)
        albumName.text = track.collectionName.trim()
        yearValue.text = track.releaseDate.substring(0,4)
        genreValue.text = track.primaryGenreName
        countryValue.text = track.country
        currentTrackTime.text = "0:00"

        Glide.with(coverImage)
            .load(General.convertURLtoBigSizeCover(track.artworkUrl100))
            .placeholder(R.drawable.placeholder_track_artwork_big)
            .fitCenter()
            .transform(RoundedCorners(General.dpToPx(8f,coverImage.context)))
            .into(coverImage)

        backButton.setOnClickListener { finish() }
    }

    private fun initalizeViews() {
        titleText = findViewById<EditText>(R.id.titleText)
        bandText = findViewById<EditText>(R.id.bandText)
        trackTime = findViewById<EditText>(R.id.trackTime)
        albumName = findViewById<EditText>(R.id.albumName)
        albumName.isSelected = true
        yearValue = findViewById<EditText>(R.id.yearValue)
        genreValue = findViewById<EditText>(R.id.genreValue)
        countryValue = findViewById<EditText>(R.id.countryValue)
        currentTrackTime = findViewById<EditText>(R.id.currentTrackTime)
        coverImage = findViewById<ImageView>(R.id.coverImage)
        backButton = findViewById<ImageButton>(R.id.backButton)

    }
}