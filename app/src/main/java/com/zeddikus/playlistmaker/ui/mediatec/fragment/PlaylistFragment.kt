package com.zeddikus.playlistmaker.ui.mediatec.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zeddikus.playlistmaker.R
import com.zeddikus.playlistmaker.databinding.FragmentPlaylistBinding
import com.zeddikus.playlistmaker.domain.mediatec.playlists.models.PlaylistPropertys
import com.zeddikus.playlistmaker.domain.search.model.Track
import com.zeddikus.playlistmaker.ui.main.activity.MainActivity
import com.zeddikus.playlistmaker.ui.mediatec.view_model.PlaylistViewModel
import com.zeddikus.playlistmaker.ui.player.fragment.PlayerFragment
import com.zeddikus.playlistmaker.ui.search.track.TracksAdapter
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.io.File
import com.zeddikus.playlistmaker.R as R1


class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TracksAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetPlaylistActions: BottomSheetBehavior<LinearLayout>
    private val defaultPeekHeight by lazy {
        (requireContext().resources.getDimension(R.dimen.track_min_height) * 4 + 16).toInt()
    }
    private val viewModel: PlaylistViewModel by inject {

        val bundle: Bundle? = arguments
        val playlistId: Int =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle?.getInt(PLAYLIST_DATA, 0) ?: 0
            } else {
                bundle?.getInt(PLAYLIST_DATA) ?: 0
            }

        parametersOf(
            playlistId
        )
    }

    companion object {
        const val PLAYLIST_DATA = "PlaylistData"
        fun createArgs(playlistId: Int): Bundle =
            bundleOf(PLAYLIST_DATA to playlistId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        activity?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).animateBottomNavigationView(View.GONE)

        val guidelineEnd = defaultPeekHeight
        binding.guidelineH.setGuidelineEnd(guidelineEnd)

        adapter = TracksAdapter(listOf<Track>(),
            { track -> clickListener(track) },
            { track -> showDialog(track) })
        binding.rvTracksInPlaylist.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTracksInPlaylist.adapter = adapter

        bottomSheetBehavior = BottomSheetBehavior.from(binding.llPlaylistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            peekHeight = defaultPeekHeight
        }

        bottomSheetPlaylistActions =
            BottomSheetBehavior.from(binding.llPlaylistsActionsBottomSheet).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }

        setListenersWatchersObservers()

    }

    private fun setListenersWatchersObservers() {

        viewModel.getShowPlayerTrigger().observe(viewLifecycleOwner) { track ->
            showPlayer(track)
        }

        viewModel.getEditPlaylistTrigger().observe(viewLifecycleOwner) { playlistId ->
            editPlaylistSettings(playlistId)
        }

        binding.tvDelete.setOnClickListener {
            bottomSheetPlaylistActions.state = BottomSheetBehavior.STATE_HIDDEN
            showDeletePlaylistDialog()
        }

        binding.ivShare.setOnClickListener {
            sharePlaylist()
        }

        binding.tvShare.setOnClickListener {
            sharePlaylist()
        }

        binding.tvEdit.setOnClickListener {
            viewModel.editPlaylistSettings()
        }

        binding.ivSettings.setOnClickListener {
            bottomSheetPlaylistActions.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        viewModel.getState().observe(viewLifecycleOwner) { playlistPropertys ->
            render(playlistPropertys)
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    private fun clickListener(track: Track) {

        if (track.previewUrl.isEmpty()) {
            Toast.makeText(
                requireContext(),
                resources.getText(R.string.error_empty_url),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            viewModel.showPlayer(track)
        }
    }

    private fun showPlayer(track: Track) {

        findNavController().navigate(
            R.id.action_playlistFragment_to_playerFragment,
            PlayerFragment.createArgs(track)
        )
    }

    private fun editPlaylistSettings(playlistId: Int?) {
        findNavController().navigate(
            R.id.action_playlistFragment_to_playlistSettingsFragment,
            PlaylistSettingsFragment.createArgs(playlistId)
        )
    }

    private fun sharePlaylist() {
        bottomSheetPlaylistActions.state = BottomSheetBehavior.STATE_HIDDEN
        if (adapter.itemCount == 0) {
            Toast.makeText(
                requireContext(),
                R.string.no_one_track_in_playlist_for_share,
                Toast.LENGTH_SHORT
            ).show()
        } else {
            viewModel.sharePlaylist()
        }
    }


    private fun closeFragment() {
        findNavController().popBackStack()
    }

    private fun deleteTrackFromPlaylist(track: Track) {
        viewModel.deleteTrackFromPlaylist(track)
    }

    private fun showDialog(track: Track) {
        val dialogWindow = MaterialAlertDialogBuilder(requireActivity())
            .setNeutralButton(getString(R1.string.cancel)) { dialog, which ->
            }
            .setPositiveButton(getString(R1.string.delete)) { dialog, which ->
                deleteTrackFromPlaylist(track)
            }.setBackground(requireContext().getDrawable(R1.drawable.btn_corners))
            .create()
        val dialogView = layoutInflater.inflate(R1.layout.alert_dialog, null)
        dialogView.findViewById<TextView>(R1.id.tvTextTitle).text =
            "${getString(R1.string.delete_track)}"
        dialogView.findViewById<TextView>(R1.id.tvTextMessage).text =
            getString(R1.string.q_delete_track_from_playlist)

        dialogWindow.setView(dialogView)
        dialogWindow.setOnShowListener {
            setValuesDialogWindow(dialogWindow)
        }

        dialogWindow.show()
    }

    private fun showDeletePlaylistDialog() {
        val dialogWindow = MaterialAlertDialogBuilder(requireActivity())
            .setNeutralButton(getString(R1.string.no)) { dialog, which ->
            }
            .setPositiveButton(getString(R1.string.yes)) { dialog, which ->
                viewModel.deletePlaylist()
                closeFragment()
            }.setBackground(requireContext().getDrawable(R1.drawable.btn_corners))
            .create()
        val dialogView = layoutInflater.inflate(R1.layout.alert_dialog, null)
        dialogView.findViewById<TextView>(R1.id.tvTextTitle).text =
            "${getString(R1.string.delete_playlist)}"
        val msg = "${getString(R1.string.q_delete_playlist)} «${binding.tvPlaylistName.text}»?"
        dialogView.findViewById<TextView>(R1.id.tvTextMessage).text = msg

        dialogWindow.setView(dialogView)
        dialogWindow.setOnShowListener {
            setValuesDialogWindow(dialogWindow)
        }

        dialogWindow.show()
    }

    private fun setValuesDialogWindow(dialogWindow: androidx.appcompat.app.AlertDialog) {
        val textColor = resources.getColor(R1.color.whiteDay_blackNight, requireContext().theme)
        val backgroundColor =
            resources.getColor(R1.color.blackDay_whiteNight, requireContext().theme)

        val btnPositive = dialogWindow.getButton(AlertDialog.BUTTON_POSITIVE)
        btnPositive.setTextColor(textColor)
        btnPositive.setBackgroundColor(backgroundColor)

        val btnCancel = dialogWindow.getButton(DialogInterface.BUTTON_NEUTRAL)
        btnCancel.setTextColor(textColor)
        btnCancel.setBackgroundColor(backgroundColor)

        dialogWindow.window?.decorView?.setBackgroundColor(backgroundColor)
        dialogWindow.window?.decorView?.setBackgroundResource(R1.drawable.alert_background)//setBackgroundColor(backgroundColor)
    }


    private fun onBackPressed() {
        closeFragment()
    }


    fun render(playlistPropertys: PlaylistPropertys) {

        binding.tvPlaylistName.text = playlistPropertys.playlist.playlistName
        binding.tvCountTracks.text = playlistPropertys.trackCount
        binding.tvDescription.text = playlistPropertys.playlist.playlistDescription
        binding.tvTimeAllTracksInPlaylist.text = playlistPropertys.playlistTime

        binding.llPlaylistTitle.tvPlaylistName.text = playlistPropertys.playlist.playlistName
        binding.llPlaylistTitle.tvCountTracks.text = playlistPropertys.trackCount

        if (playlistPropertys.playlist.playlistCover.isNotEmpty()) {
            binding.ivCoverImage.scaleType = ImageView.ScaleType.CENTER_CROP
            val filePath = File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "album_playlist_covers"
            )
            val file = File(filePath, playlistPropertys.playlist.playlistCover)
            binding.ivCoverImage.setImageURI(file.toUri())

            binding.llPlaylistTitle.sivCover.setImageURI(file.toUri())
        } else {
            binding.ivCoverImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
            binding.ivCoverImage.setImageDrawable(requireContext().getDrawable(R.drawable.placeholder_track_artwork_big)!!)
            binding.llPlaylistTitle.sivCover.setImageDrawable(requireContext().getDrawable(R.drawable.placeholder_track_artwork)!!)
        }

        adapter.setNewList(playlistPropertys.tracks)
        bottomSheetPlaylistActions.state = BottomSheetBehavior.STATE_HIDDEN

        updateBottomSheetBehavior()

    }

    private fun updateBottomSheetBehavior() {

        if (adapter.itemCount == 0) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            binding.tvTrackListEmpty.isVisible = true
            binding.rvTracksInPlaylist.isVisible = false
        } else {

            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                bottomSheetBehavior.peekHeight = defaultPeekHeight
            }
            binding.tvTrackListEmpty.isVisible = false
            binding.rvTracksInPlaylist.isVisible = true
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.update()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}