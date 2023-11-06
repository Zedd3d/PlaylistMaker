package com.zeddikus.playlistmaker.presentation.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.zeddikus.playlistmaker.Creator
import com.zeddikus.playlistmaker.utils.General
import com.zeddikus.playlistmaker.domain.models.PlayerState
import com.zeddikus.playlistmaker.R
import com.zeddikus.playlistmaker.databinding.ActivityPlayerBinding
import com.zeddikus.playlistmaker.domain.api.MediaPlayer
import com.zeddikus.playlistmaker.domain.api.genericType
import com.zeddikus.playlistmaker.domain.models.Track
import java.time.Instant
import java.time.ZoneId

class PlayerActivity : AppCompatActivity() {


    private lateinit var binding: ActivityPlayerBinding
    private lateinit var track: Track
    private lateinit var trackTimeUpdater: Runnable
    private lateinit var mediaPlayer: MediaPlayer
    private var playerState = PlayerState.DEFAULT
    private var runnableSetPlayerState: Runnable? = null
    private var jsonHandler = Creator.provideJsonHandler()
    private companion object {
        private val mainHandler = Handler(Looper.getMainLooper())
        private const val UPDATE_TRACK_TIME_DELAY = 300L
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)

        track = jsonHandler.fromJson<Track>(intent.getStringExtra("Track")?:"", jsonHandler.genericType<Track>())
        binding.titleText.text = track.trackName
        binding.bandText.text = track.artistName
        binding.trackTime.text = track.trackTime
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
            .transform(RoundedCorners(General.dpToPx(8f, binding.coverImage.context)))
            .into(binding.coverImage)

        binding.backButton.setOnClickListener { finish() }

        defaultProgressValues()
        setPlayerState(PlayerState.DEFAULT)

        trackTimeUpdater = object : Runnable {
            override fun run() {
                updateTrackTime()
                mainHandler.postDelayed(
                    trackTimeUpdater,
                    UPDATE_TRACK_TIME_DELAY
                )
            }
        }

        mediaPlayer = Creator.provideAudioPlayerInteractor()
        mediaPlayer.setConsumer { playerState -> setPlayerState(playerState) }
        mediaPlayer.preparePlayer(track.previewUrl)
    }

    private fun setPlayerState(state: PlayerState){
        playerState = state

        runnableSetPlayerState?.let {
                prevRunnable -> mainHandler.removeCallbacks(prevRunnable)
        }

        val newRunnable = Runnable{
            when (state){
                //PlayerState.DEFAULT -> preparePlayer()
                PlayerState.PREPARED -> {
                    mainHandler.removeCallbacks(trackTimeUpdater)
                    defaultProgressValues()
                    readyToPlay()
                }
                PlayerState.PLAYING -> startPlayer()
                PlayerState.PAUSED -> pausedPlayer()
                PlayerState.STOPPED -> stopPlayer()
                PlayerState.PREPAIRING_ERROR -> showPrepairingError()
                PlayerState.PREPAIRING -> binding.progressBarOnPrepare.visibility = View.VISIBLE
                else -> null
            }
        }

        runnableSetPlayerState = newRunnable

        mainHandler.post(newRunnable)

        Glide.with(this).load(getImageOnState(state)).dontTransform().into(binding.playButton)
    }

    private fun showPrepairingError() {
        Toast.makeText(this,resources.getText(R.string.error_prepairing_media_player),Toast.LENGTH_LONG).show()
        finish()
    }

    fun startPlayer() {
        mediaPlayer.start()
        mainHandler.postDelayed(
            trackTimeUpdater, UPDATE_TRACK_TIME_DELAY
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
        binding.progressBar.max = (track.trackTimeMillis / 1000).toInt();
    }

    fun updateTrackTime(){
        val currentProgress = mediaPlayer.getCurrentProgress()
        binding.progressBar.progress = currentProgress.progress
        binding.currentTrackTime.text = currentProgress.currentTime
    }

    private fun getImageOnState(state: PlayerState): Int {
        return when (state){
            PlayerState.PLAYING -> R.drawable.ic_pause
            else  -> R.drawable.ic_play
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