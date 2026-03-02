package com.example.playlistmaker.sharing.domain

import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.data.ExternalNavigator

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val resourcesProvider: ResourcesProvider
) : SharingInteractor {
    override fun shareApp() {
        val link = resourcesProvider.getString(R.string.share_message)
        externalNavigator.shareLink(link)
    }

    override fun openTerms() {
        val link = resourcesProvider.getString(R.string.agreement_url)
        externalNavigator.openLink(link)
    }

    override fun openSupport() {
        val email = resourcesProvider.getString(R.string.support_mail)
        val subject = resourcesProvider.getString(R.string.support_title)
        val text = resourcesProvider.getString(R.string.support_text)
        externalNavigator.openEmail(email, subject, text)
    }
}