package com.zeddikus.playlistmaker.ui.mediatec.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.zeddikus.playlistmaker.R
import com.zeddikus.playlistmaker.databinding.FragmentFavoritesBinding
import com.zeddikus.playlistmaker.ui.mediatec.view_model.FavoritesViewModel
import org.koin.android.ext.android.inject

class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val viewModel: FavoritesViewModel by inject()
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = FavoritesFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var placeholderBinding = binding.placeholderTrouble
        if (!(activity == null)) {
            placeholderBinding.placeholderTroubleText.text = getText(R.string.favorites_empty)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}