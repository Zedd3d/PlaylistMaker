package com.zeddikus.playlistmaker.ui.player.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.zeddikus.playlistmaker.R
import com.zeddikus.playlistmaker.databinding.ActivityPlayerBinding
import com.zeddikus.playlistmaker.domain.player.models.MediaPlayerProgress
import com.zeddikus.playlistmaker.domain.player.models.PlayerState
import com.zeddikus.playlistmaker.domain.sharing.model.Track
import com.zeddikus.playlistmaker.ui.player.view_model.PlayerViewModel
import com.zeddikus.playlistmaker.utils.General
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.time.Instant
import java.time.ZoneId

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val viewModel: PlayerViewModel by inject {
        parametersOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(TRACK_DATA, Track::class.java)!!
            } else {
                intent.getParcelableExtra(TRACK_DATA)!!
            }
        )
    }

    companion object {
        const val TRACK_DATA = "TrackData"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.playButton.setOnClickListener {
            viewModel.pushPlayButton()
        }

        viewModel.onLoad().observe(this) {
            setTrackPropertys(it)
        }

        viewModel.getState().observe(this) {
            render(it)
        }

        viewModel.getProgress().observe(this) {
            updateTrackTime(it)
        }
    }

    private fun setTrackPropertys(track: Track) {
        binding.progressBar.max = (track.trackTimeMillis / 1000).toInt()
        binding.titleText.text = track.trackName
        binding.bandText.text = track.artistName
        binding.trackTime.text = track.trackTime
        binding.albumName.text = track.collectionName.trim()
        binding.albumName.isSelected = true
        binding.yearValue.text =
            Instant.parse(track.releaseDate).atZone(ZoneId.systemDefault()).year.toString()
        binding.genreValue.text = track.primaryGenreName
        binding.countryValue.text = track.country

        Glide.with(binding.coverImage)
            .load(General.convertURLtoBigSizeCover(track.artworkUrl100))
            .placeholder(R.drawable.placeholder_track_artwork_big)
            .fitCenter()
            .transform(RoundedCorners(General.dpToPx(8f, binding.coverImage.context)))
            .into(binding.coverImage)
    }

    private fun render(state: PlayerState) {
        when (state) {
            PlayerState.PREPAIRING_ERROR -> showPrepairingError()
            PlayerState.PREPARED -> binding.progressBarOnPrepare.visibility = View.GONE
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