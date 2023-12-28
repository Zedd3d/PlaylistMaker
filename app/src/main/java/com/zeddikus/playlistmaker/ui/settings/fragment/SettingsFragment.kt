package com.zeddikus.playlistmaker.ui.settings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zeddikus.playlistmaker.databinding.FragmentSettingsBinding
import com.zeddikus.playlistmaker.ui.settings.state.SettingsState
import com.zeddikus.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.android.ext.android.inject

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by inject()
    lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)

        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.llShare.setOnClickListener { viewModel.shareApp() }
        binding.llSupport.setOnClickListener { viewModel.mailToSupport() }
        binding.llTerms.setOnClickListener { viewModel.showTerms() }

        binding.swBlackTheme.setOnClickListener { viewModel.toggleThemeSwitch() }

        viewModel.getState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is SettingsState.Content -> binding.swBlackTheme.isChecked = state.isDarkTheme
            }
        }
    }
}