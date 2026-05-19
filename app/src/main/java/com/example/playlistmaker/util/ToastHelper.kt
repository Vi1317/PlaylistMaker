package com.example.playlistmaker.util

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.example.playlistmaker.databinding.CustomToastBinding

fun showCustomToast(context: Context, message: String) {
    val binding = CustomToastBinding.inflate(LayoutInflater.from(context))
    binding.toastText.text = message

    Toast(context).apply {
        duration = Toast.LENGTH_SHORT
        view = binding.root
        show()
    }
}