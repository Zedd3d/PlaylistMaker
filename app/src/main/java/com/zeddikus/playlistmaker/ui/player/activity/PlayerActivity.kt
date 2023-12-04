package com.zeddikus.playlistmaker.ui.player.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zeddikus.playlistmaker.R
import com.zeddikus.playlistmaker.databinding.ActivityPlayerBinding
import com.zeddikus.playlistmaker.domain.player.models.MediaPlayerProgress
import com.zeddikus.playlistmaker.domain.player.models.PlayerState
import com.zeddikus.playlistmaker.domain.sharing.model.Track
import com.zeddikus.playlistmaker.ui.player.view_model.PlayerViewModel
import com.zeddikus.playlistmaker.utils.General
import java.time.Instant
import java.time.ZoneId

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var track: Track
    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)



        track = Gson().fromJson<Track>(
            intent.getStringExtra("Track") ?: "",
            object : TypeToken<Track>() {}.type
        )
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            fun factory(track: Track): ViewModelProvider.Factory {
                return viewModelFactory {
                    initializer {
                        PlayerViewModel(track)
                    }
                }
            }
        }.factory(track))[PlayerViewModel::class.java]
        binding.titleText.text = track.trackName
        binding.bandText.text = track.artistName
        binding.trackTime.text = track.trackTime
        binding.albumName.text = track.collectionName.trim()
        binding.albumName.isSelected = true
        binding.yearValue.text =
            Instant.parse(track.releaseDate).atZone(ZoneId.systemDefault()).year.toString()
        binding.genreValue.text = track.primaryGenreName
        binding.countryValue.text = track.country

        binding.playButton.setOnClickListener {
            viewModel.pushPlayButton()
        }

        Glide.with(binding.coverImage)
            .load(General.convertURLtoBigSizeCover(track.artworkUrl100))
            .placeholder(R.drawable.placeholder_track_artwork_big)
            .fitCenter()
            .transform(RoundedCorners(General.dpToPx(8f, binding.coverImage.context)))
            .into(binding.coverImage)

        binding.backButton.setOnClickListener { finish() }


        setPlayerState(PlayerState.DEFAULT)

        viewModel.getState().observe(this) {
            setPlayerState(it)
        }

        viewModel.getProgress().observe(this) {
            updateTrackTime(it)
        }

        viewModel.clearProgress()

    }

    private fun setPlayerState(state: PlayerState) {
        when (state) {
            PlayerState.PREPARED -> {
                viewModel.clearProgress()
                readyToPlay()
            }

            PlayerState.PLAYING -> viewModel.startPlayer()
            PlayerState.PAUSED -> viewModel.pausedPlayer()
            PlayerState.STOPPED -> viewModel.stopPlayer()
            PlayerState.PREPAIRING_ERROR -> showPrepairingError()
            PlayerState.PREPAIRING -> binding.progressBarOnPrepare.visibility = View.VISIBLE
            else -> null
        }

        Glide.with(this).load(getImageOnState(state)).dontTransform().into(binding.playButton)
    }

    private fun showPrepairingError() {
        Toast.makeText(
            this,
            resources.getText(R.string.error_prepairing_media_player),
            Toast.LENGTH_LONG
        ).show()
        finish()
    }


    private fun readyToPlay(){
        binding.progressBarOnPrepare.visibility = View.GONE
        binding.progressBar.max = (track.trackTimeMillis / 1000).toInt();
    }

    private fun updateTrackTime(currentProgress: MediaPlayerProgress) {
        binding.progressBar.progress = currentProgress.progress
        binding.currentTrackTime.text = currentProgress.currentTime
    }

    private fun getImageOnState(state: PlayerState): Int {
        return when (state){
            PlayerState.PLAYING -> R.drawable.ic_pause
            else  -> R.drawable.ic_play
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.release()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausedPlayer()
    }

}