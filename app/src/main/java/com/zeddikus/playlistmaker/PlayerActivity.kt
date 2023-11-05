package com.zeddikus.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zeddikus.playlistmaker.databinding.ActivityPlayerBinding
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private companion object {
        private val mainHandler = Handler(Looper.getMainLooper())
        private const val UPDATE_TRACK_TIME_DELAY = 300L
    }

    lateinit var binding: ActivityPlayerBinding
    lateinit var track: Track
    lateinit var trackTimeUpdater: Runnable
    private var mediaPlayer = MediaPlayer()
    private var playerState = PlayerState.DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)

        track = Gson().fromJson<Track>(intent.getStringExtra("Track"), object: TypeToken<Track>(){}.type)
        binding.titleText.text = track.trackName
        binding.bandText.text = track.artistName
        binding.trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime)
        binding.albumName.text = track.collectionName.trim()
        binding.albumName.isSelected = true
        binding.yearValue.text = Instant.parse(track.releaseDate).atZone(ZoneId.systemDefault()).year.toString()
        binding.genreValue.text = track.primaryGenreName
        binding.countryValue.text = track.country

        binding.playButton.setOnClickListener {
            when (playerState){
                PlayerState.PAUSED -> setPlayerState(PlayerState.PLAYING)
                PlayerState.STOPPED -> setPlayerState(PlayerState.PLAYING)
                PlayerState.PREPARED -> setPlayerState(PlayerState.PLAYING)
                PlayerState.PLAYING -> setPlayerState(PlayerState.PAUSED)
                else -> null
            }
        }

        Glide.with(binding.coverImage)
            .load(General.convertURLtoBigSizeCover(track.artworkUrl100))
            .placeholder(R.drawable.placeholder_track_artwork_big)
            .fitCenter()
            .transform(RoundedCorners(General.dpToPx(8f,binding.coverImage.context)))
            .into(binding.coverImage)

        binding.backButton.setOnClickListener { finish() }

        trackTimeUpdater = object : Runnable {
            override fun run() {
                updateTrackTime()
                mainHandler.postDelayed(trackTimeUpdater,
                    UPDATE_TRACK_TIME_DELAY
                )
            }
        }

        defaultProgressValues()
        setPlayerState(PlayerState.DEFAULT)
    }

    private fun setPlayerState(state: PlayerState){
        playerState = state
        when (state){
            PlayerState.DEFAULT -> preparePlayer()
            PlayerState.PREPARED -> readyToPlay()
            PlayerState.PLAYING -> startPlayer()
            PlayerState.PAUSED -> pausedPlayer()
            PlayerState.STOPPED -> stopPlayer()
            else -> null
        }
        Glide.with(this).load(getImageOnState(state)).dontTransform().into(binding.playButton)
    }

    fun startPlayer() {
        mediaPlayer.start()

        mainHandler.postDelayed(
            trackTimeUpdater
            , UPDATE_TRACK_TIME_DELAY
        )
    }

    private fun pausedPlayer() {
        mediaPlayer.pause()
        mainHandler.removeCallbacks(trackTimeUpdater)
    }

    private fun stopPlayer() {
        mediaPlayer.stop()
        mainHandler.removeCallbacks(trackTimeUpdater)
    }
    private fun readyToPlay(){
        binding.progressBarOnPrepare.visibility = View.GONE
        binding.progressBar.max = (track.trackTime / 1000).toInt();
    }

    fun updateTrackTime(){
        binding.progressBar.progress = (mediaPlayer.currentPosition / 1000)
        binding.currentTrackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    private fun getImageOnState(state: PlayerState): Int {
        return when (state){
            PlayerState.PLAYING -> R.drawable.ic_pause
            else  -> R.drawable.ic_play
        }
    }

    private fun preparePlayer() {
        setPlayerState(PlayerState.PREPAIRING)
        binding.progressBarOnPrepare.visibility = View.VISIBLE
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            setPlayerState(PlayerState.PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            mainHandler.removeCallbacks(trackTimeUpdater)
            defaultProgressValues()
            setPlayerState(PlayerState.PREPARED)
        }
    }

    private fun defaultProgressValues() {
        binding.progressBar.progress = 0
        binding.currentTrackTime.text = "00:00"
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    override fun onPause() {
        super.onPause()
        setPlayerState(PlayerState.PAUSED)
    }

}