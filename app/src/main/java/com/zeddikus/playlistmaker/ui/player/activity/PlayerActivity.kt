package com.zeddikus.playlistmaker.ui.player.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.zeddikus.playlistmaker.R
import com.zeddikus.playlistmaker.databinding.ActivityPlayerBinding
import com.zeddikus.playlistmaker.domain.player.models.MediaPlayerProgress
import com.zeddikus.playlistmaker.domain.player.models.PlayerState
import com.zeddikus.playlistmaker.domain.player.models.TrackState
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

        val bundle: Bundle? = intent.extras
        val track: Track?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            track = bundle?.getParcelable(TRACK_DATA, Track::class.java)
        } else {
            track = bundle?.getParcelable<Track>(TRACK_DATA)
        }

        parametersOf(
            track!!
        )
    }

    companion object {
        const val TRACK_DATA = "TrackData"
        fun createArgs(track: Track): Bundle =
            bundleOf(TRACK_DATA to track)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)

        binding.playButton.setOnClickListener {
            viewModel.pushPlayButton()
        }

        binding.addToFavorites.setOnClickListener {
            viewModel.addingToFavorites()
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

    private fun setTrackPropertys(trackState: TrackState) {
        val track = trackState.track
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

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        Glide.with(binding.coverImage)
            .load(General.convertURLtoBigSizeCover(track.artworkUrl100))
            .placeholder(R.drawable.placeholder_track_artwork_big)
            .fitCenter()
            .transform(RoundedCorners(General.dpToPx(8f, binding.coverImage.context)))
            .into(binding.coverImage)

        //Стоит на курсе написать, что библиотека весьма и весьма плохо работает с ночной темой
        Glide.with(this)
            .load(ContextCompat.getDrawable(this, getFavoriteImageOnState(trackState)))
            .into(binding.addToFavorites)
    }

    private fun render(state: PlayerState) {
        when (state) {
            PlayerState.PREPAIRING_ERROR -> showPrepairingError()
            PlayerState.PREPARED -> binding.progressBarOnPrepare.visibility = View.GONE
            PlayerState.PREPAIRING -> binding.progressBarOnPrepare.visibility = View.VISIBLE
            else -> null
        }

        Glide.with(this)
            .load(getImageOnState(state))
            .dontTransform()
            .into(binding.playButton)
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
        return when (state) {
            PlayerState.PLAYING -> R.drawable.ic_pause
            else -> R.drawable.ic_play
        }
    }

    private fun getFavoriteImageOnState(state: TrackState): Int {
        return if (state.isFavorite) R.drawable.ic_favorites_on else R.drawable.ic_favorites_off
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