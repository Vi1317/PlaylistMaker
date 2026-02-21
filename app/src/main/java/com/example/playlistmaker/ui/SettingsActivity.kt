package com.example.playlistmaker.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.Creator
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        val backButton = findViewById<MaterialToolbar>(R.id.back)
        backButton.setNavigationOnClickListener {
            finish()
        }

        val themeSwitch = findViewById<SwitchMaterial>(R.id.theme_switch)
        val settingsInteractor = Creator.provideSettingsInteractor(this)
        themeSwitch.isChecked = settingsInteractor.isDarkTheme()
        themeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            settingsInteractor.switchTheme(isChecked)
        }

        val shareButton = findViewById<MaterialTextView>(R.id.share)
        shareButton.setOnClickListener {
            val shareIntent: Intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message))
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
        }

        val supportButton = findViewById<MaterialTextView>(R.id.support)
        supportButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:".toUri()
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_mail)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_title))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_text))
            }
            startActivity(emailIntent)
        }

        val agreementButton = findViewById<MaterialTextView>(R.id.agreement)
        agreementButton.setOnClickListener {
            val openIntent = Intent(Intent.ACTION_VIEW).apply {
                data = getString(R.string.agreement_url).toUri()
            }
            startActivity(openIntent)
        }
    }
}