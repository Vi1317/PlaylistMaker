package com.example.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.ACTION_SENDTO
import android.content.Intent.ACTION_VIEW
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.core.net.toUri

class ExternalNavigator(private val context: Context) {
    fun shareLink(link: String) {
        val intent = Intent(ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)
        }
        val chooser = Intent.createChooser(intent, "Поделиться")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    fun openLink(link: String) {
        Intent(ACTION_VIEW).apply {
            data = link.toUri()
            addFlags(FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(this)
        }
    }

    fun openEmail(email: String, subject: String, text: String) {
        Intent(ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(this)
        }
    }
}