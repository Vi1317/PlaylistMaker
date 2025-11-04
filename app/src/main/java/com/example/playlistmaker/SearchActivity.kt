package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.core.widget.addTextChangedListener
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {
    private lateinit var searchText: EditText
    private lateinit var clearButton: ImageView
    private var currentText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search)

        val backButton = findViewById<MaterialToolbar>(R.id.back)
        backButton.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        searchText = findViewById<EditText>(R.id.search_text)
        clearButton = findViewById<ImageView>(R.id.clear_search)

        clearButton.setOnClickListener {
            searchText.setText("")
            clearButton.visibility = View.GONE

            val imm = getSystemService<InputMethodManager>()
            imm?.hideSoftInputFromWindow(searchText.windowToken, 0)
        }

        searchText.addTextChangedListener { text ->
            clearButton.visibility = clearButtonVisibility(text)
            currentText = text?.toString() ?: ""
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    companion object {
        private const val SEARCH_TEXT = "search_text"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, currentText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredText = savedInstanceState.getString(SEARCH_TEXT, "")
        searchText.setText(restoredText)
    }
}