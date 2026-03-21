package com.example.playlistmaker.media.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentsAdapter(
    host: FragmentActivity
): FragmentStateAdapter(host) {
    override fun createFragment(position: Int): Fragment {
        return if (position == 0) FavoriteFragment() else PlaylistFragment()
    }

    override fun getItemCount(): Int {
        return 2
    }

}