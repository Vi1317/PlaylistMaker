package com.example.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.ACTION_SENDTO
import android.content.Intent.ACTION_VIEW
import androidx.core.net.toUri

class ExternalNavigator(private val context: Context) {
    fun shareLink(link: String) {
        Intent(ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)
            context.startActivity(Intent.createChooser(this, "Поделиться"))
        }
    }

    fun openLink(link: String) {
        Intent(ACTION_VIEW).apply {
            data = link.toUri()
            context.startActivity(this)
        }
    }

    fun openEmail(email: String, subject: String, text: String) {
        Intent(ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
            context.startActivity(this)
        }
    }
}