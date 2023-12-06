package com.zeddikus.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.zeddikus.playlistmaker.creator.Creator
import com.zeddikus.playlistmaker.domain.settings.SharingInteractor
import com.zeddikus.playlistmaker.domain.settings.api.ThemeChanger
import com.zeddikus.playlistmaker.ui.settings.state.SettingsState

class SettingsViewModel(
    private val themeChanger: ThemeChanger,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val state = MutableLiveData<SettingsState>()

    init {
        state.value = SettingsState.Content(themeChanger.getCurrentTheme())
    }

    companion object {
        fun factory(): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    SettingsViewModel(
                        Creator.provideThemeChanger(),
                        Creator.provideSharingInteractor()
                    )
                }
            }
        }
    }

    fun getState(): LiveData<SettingsState> = state


    fun showTerms() {
        sharingInteractor.showTerms()
    }

    fun mailToSupport() {
        sharingInteractor.mailToSupport()
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun toggleThemeSwitch() {
        val switchValue = !themeChanger.getCurrentTheme()
        themeChanger.saveSwitchDarkTheme(switchValue)
        state.value = SettingsState.Content(switchValue)
    }


}