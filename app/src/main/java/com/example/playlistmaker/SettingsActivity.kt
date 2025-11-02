package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView
import androidx.core.net.toUri
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        val backButton = findViewById<MaterialToolbar>(R.id.back)
        backButton.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val themeSwitch = findViewById<SwitchMaterial>(R.id.theme_switch)
        themeSwitch.setOnCheckedChangeListener {buttonView, isChecked ->
            if(isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        val shareButton = findViewById<MaterialTextView>(R.id.share)
        shareButton.setOnClickListener {
            val shareIntent: Intent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message))
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
        }

        val supportButton = findViewById<MaterialTextView>(R.id.support)
        supportButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:".toUri()
                putExtra(Intent.EXTRA_EMAIL, getString(R.string.support_mail))
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