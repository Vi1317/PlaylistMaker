package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.sharing.domain.ResourcesProvider

class ResourcesProviderImpl(private val context: Context) : ResourcesProvider {
    override fun getString(resId: Int): String = context.getString(resId)
}