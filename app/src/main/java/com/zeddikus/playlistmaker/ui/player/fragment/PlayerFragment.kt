package com.zeddikus.playlistmaker.ui.player.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.zeddikus.playlistmaker.R
import com.zeddikus.playlistmaker.databinding.FragmentPlayerBinding
import com.zeddikus.playlistmaker.domain.mediatec.playlists.models.Playlist
import com.zeddikus.playlistmaker.domain.mediatec.playlists.models.TrackInPlaylistState
import com.zeddikus.playlistmaker.domain.player.models.MediaPlayerProgress
import com.zeddikus.playlistmaker.domain.player.models.PlayerState
import com.zeddikus.playlistmaker.domain.player.models.TrackState
import com.zeddikus.playlistmaker.domain.search.model.Track
import com.zeddikus.playlistmaker.ui.main.activity.MainActivity
import com.zeddikus.playlistmaker.ui.mediatec.playlists.PlaylistsAdapter
import com.zeddikus.playlistmaker.ui.player.view_model.PlayerViewModel
import com.zeddikus.playlistmaker.utils.General
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.time.Instant
import java.time.ZoneId


class PlayerFragment : Fragment() {

    private lateinit var binding: FragmentPlayerBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val viewModel: PlayerViewModel by inject {

        val bundle: Bundle? = arguments
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

    private lateinit var adapter: PlaylistsAdapter

    companion object {
        const val TRACK_DATA = "TrackData"
        fun createArgs(track: Track): Bundle =
            bundleOf(TRACK_DATA to track)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        activity?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        )
        binding = FragmentPlayerBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerPlaylists.layoutManager = LinearLayoutManager(requireContext())
        adapter = PlaylistsAdapter(listOf<Playlist>(), false) { playlist: Playlist ->
            clickListener(
                playlist
            )
        }
        binding.recyclerPlaylists.adapter = adapter

        setListenersObservers()

        (activity as MainActivity).animateBottomNavigationView(View.GONE)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // newState — новое состояние BottomSheet
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        blackout(false)
                    }

                    else -> {
                        // Остальные состояния не обрабатываем
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    private fun setListenersObservers() {
        binding.playButton.setOnClickListener {
            viewModel.pushPlayButton()
        }

        binding.addToFavorites.setOnClickListener {
            viewModel.addingToFavorites()
        }

        binding.addToPlaylistButton.setOnClickListener {
            viewModel.addingToPlaylist()
        }

        binding.blackout.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            blackout(false)
        }

        binding.createNewPlaylistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            findNavController().navigate(
                R.id.action_playerFragment_to_playlistFragment
            )
        }

        viewModel.onLoad().observe(viewLifecycleOwner) {
            setTrackPropertys(it)
        }

        viewModel.getState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.getStateAddingToPlaylists().observe(viewLifecycleOwner) {
            addindToPlaylist(it)
        }

        viewModel.getProgress().observe(viewLifecycleOwner) {
            updateTrackTime(it)
        }

        viewModel.getTrackAdditionStatus().observe(viewLifecycleOwner) {
            renderPlaylists(it)
        }
    }

    private fun renderPlaylists(state: TrackInPlaylistState) {
        when (state) {
            is TrackInPlaylistState.AleradyIn -> {
                Toast.makeText(
                    requireContext(),
                    "${getString(R.string.track_alerady_in_playlist)} ${state.playlistName}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is TrackInPlaylistState.AdditionSuccessful -> {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                Toast.makeText(
                    requireContext(),
                    "${getString(R.string.track_added_in_playlist)} ${state.playlistName}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun clickListener(playlist: Playlist) {
        viewModel.addTrackToPlayList(playlist)
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
            findNavController().popBackStack()
        }

        Glide.with(binding.coverImage)
            .load(General.convertURLtoBigSizeCover(track.artworkUrl100))
            .placeholder(R.drawable.placeholder_track_artwork_big)
            .fitCenter()
            .transform(RoundedCorners(General.dpToPx(8f, binding.coverImage.context)))
            .into(binding.coverImage)

        //Стоит на курсе написать, что библиотека весьма и весьма плохо работает с ночной темой
        Glide.with(this)
            .load(
                ContextCompat.getDrawable(
                    requireContext(),
                    getFavoriteImageOnState(trackState.isFavorite)
                )
            )
            .into(binding.addToFavorites)
    }

    private fun render(state: PlayerState) {
        when (state) {
            PlayerState.PREPAIRING_ERROR -> showPrepairingError()
            PlayerState.PREPARED -> binding.progressBarOnPrepare.visibility = View.GONE
            PlayerState.PREPAIRING -> binding.progressBarOnPrepare.visibility = View.VISIBLE
            else -> binding.progressBarOnPrepare.visibility = View.GONE
        }

        Glide.with(this)
            .load(getImageOnState(state))
            .dontTransform()
            .into(binding.playButton)
    }

    private fun showPrepairingError() {
        Toast.makeText(
            requireContext(),
            resources.getText(R.string.error_prepairing_media_player),
            Toast.LENGTH_LONG
        ).show()
        findNavController().popBackStack()
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

    private fun getFavoriteImageOnState(isFavorite: Boolean): Int {
        return if (isFavorite) R.drawable.ic_favorites_on else R.drawable.ic_favorites_off
    }

    private fun addindToPlaylist(playlists: List<Playlist>) {
        showPlaylists(playlists)
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        blackout(true)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        adapter.setNewList(playlists)

    }

    private fun blackout(fillBlackout: Boolean) {
        var layout_blackout = binding.blackout

        if (fillBlackout) {
            val anim = ObjectAnimator.ofFloat(layout_blackout, "alpha", 0.5f)
            anim.setDuration(300) // duration 300 ms
            anim.start()
            anim.addListener(object : AnimatorListenerAdapter() {
            })
            layout_blackout.visibility = View.VISIBLE
            layout_blackout.isClickable = true
            layout_blackout.isFocusable = true
            layout_blackout.isEnabled = true
        } else {
            val anim = ObjectAnimator.ofFloat(layout_blackout, "alpha", 0.0f)
            anim.setDuration(300)
            anim.start()
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    layout_blackout.visibility = View.GONE
                    layout_blackout.isClickable = false
                    layout_blackout.isFocusable = false
                    layout_blackout.isEnabled = false
                }
            })
        }

        var coverImage = binding.coverImage
        if (fillBlackout) {
            val anim = ObjectAnimator.ofFloat(coverImage, "alpha", 0.5f)
            anim.setDuration(300) // duration 300 ms
            anim.start()
            anim.addListener(object : AnimatorListenerAdapter() {
            })
        } else {
            val anim = ObjectAnimator.ofFloat(coverImage, "alpha", 1.0f)
            anim.setDuration(300)
            anim.start()
            anim.addListener(object : AnimatorListenerAdapter() {
            })
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