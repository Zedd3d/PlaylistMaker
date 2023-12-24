package com.zeddikus.playlistmaker.ui.mediatec.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.zeddikus.playlistmaker.R
import com.zeddikus.playlistmaker.databinding.FragmentPlaylistsBinding
import com.zeddikus.playlistmaker.databinding.PlaceholderEmptyErrorBinding
import com.zeddikus.playlistmaker.ui.mediatec.view_model.PlaylistsViewModel
import org.koin.android.ext.android.inject


class PlaylistsFragment : Fragment() {
    private lateinit var binding: FragmentPlaylistsBinding
    lateinit var placeholderBinding: PlaceholderEmptyErrorBinding

    private val viewModel: PlaylistsViewModel by inject()

    companion object {
        fun newInstance() = PlaylistsFragment().apply {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        placeholderBinding = PlaceholderEmptyErrorBinding.inflate(layoutInflater)
        if (!(activity == null)) {

            placeholderBinding.placeholderTroubleText.text = getText(R.string.playlists_empty)
            binding.layoutPlaylists.addView(placeholderBinding.root)
            placeholderBinding.placeholderTroubleButton.visibility = View.GONE

            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.root)
            constraintSet.connect(
                placeholderBinding.root.id,
                ConstraintSet.TOP,
                binding.root.id,
                ConstraintSet.TOP,
                0
            )

            constraintSet.connect(
                placeholderBinding.root.id,
                ConstraintSet.START,
                binding.root.id,
                ConstraintSet.START,
                0
            )

            constraintSet.connect(
                placeholderBinding.root.id,
                ConstraintSet.END,
                binding.root.id,
                ConstraintSet.END,
                0
            )
            constraintSet.applyTo(binding.root)
        }
    }
}