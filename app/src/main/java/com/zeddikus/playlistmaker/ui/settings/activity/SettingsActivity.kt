package com.zeddikus.playlistmaker.ui.settings.activity


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.zeddikus.playlistmaker.databinding.ActivitySettingsBinding
import com.zeddikus.playlistmaker.ui.settings.state.SettingsState
import com.zeddikus.playlistmaker.ui.settings.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel
    lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val viewRoot = binding.root
        setContentView(viewRoot)

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.factory()
        )[SettingsViewModel::class.java]

        binding.llShare.setOnClickListener { viewModel.shareApp() }
        binding.llSupport.setOnClickListener { viewModel.mailToSupport() }
        binding.llTerms.setOnClickListener { viewModel.showTerms() }

        binding.swBlackTheme.setOnClickListener { viewModel.toggleThemeSwitch() }

        binding.imgBtnBack.setOnClickListener {
            finish()
        }

        viewModel.getState().observe(this) { state ->
            when (state) {
                is SettingsState.Content -> binding.swBlackTheme.isChecked = state.isDarkTheme
            }
        }

    }

}