package com.example.playlistmaker.sharing.domain

interface ResourcesProvider {
    fun getString(resId: Int): String
}