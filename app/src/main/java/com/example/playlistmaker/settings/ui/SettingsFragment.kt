package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.playlistmaker.settings.viewmodel.SettingsViewModel
import com.example.playlistmaker.ui.PlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val isDarkTheme by viewModel.themeState.observeAsState(false)

                PlaylistMakerTheme(darkTheme = isDarkTheme)  {
                    SettingsScreen(
                        isDarkTheme = isDarkTheme,
                        onThemeChanged = { isChecked ->
                            viewModel.switchTheme(isChecked)
                        },
                        onShareClick = { viewModel.shareApp() },
                        onSupportClick = { viewModel.openSupport() },
                        onAgreementClick = { viewModel.openTerms() }
                    )
                }
            }
        }
    }
}