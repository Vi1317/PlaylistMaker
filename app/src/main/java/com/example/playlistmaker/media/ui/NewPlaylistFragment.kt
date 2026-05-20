package com.example.playlistmaker.media.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.media.viewmodel.NewPlaylistViewModel
import com.example.playlistmaker.util.showCustomToast
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {
    private lateinit var binding: FragmentNewPlaylistBinding
    private var selectedImageUri: Uri? = null
    private var hasChanges = false
    private val viewModel: NewPlaylistViewModel by viewModel()

    private val pickMediaLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            binding.playlistCover.scaleType = ImageView.ScaleType.CENTER_CROP
            binding.playlistCover.setImageURI(uri)
            saveImageToPrivateStorage(uri)
            hasChanges = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.back.setNavigationOnClickListener {
            if (hasChanges) {
                showExitDialog()
            } else {
                findNavController().navigateUp()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (hasChanges) {
                        showExitDialog()
                    } else {
                        findNavController().navigateUp()
                    }
                }
            }
        )

        binding.playlistCover.setOnClickListener {
            pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.create.setOnClickListener {
            createPlaylist()
        }

        binding.playlistName.addTextChangedListener { text ->
            binding.create.isEnabled = !text.isNullOrEmpty()
            hasChanges = true
        }

        binding.playlistName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.playlistDescription.requestFocus()
                true
            } else {
                false
            }
        }

        binding.playlistDescription.addTextChangedListener {
            hasChanges = true
        }

        binding.playlistDescription.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard()
                true
            } else {
                false
            }
        }
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        val filePath =
            File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val fileName = "playlist_${System.currentTimeMillis()}.jpg"
        val file = File(filePath, fileName)

        requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                BitmapFactory.decodeStream(inputStream)
                    .compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        }
        selectedImageUri = file.toUri()
    }

    private fun createPlaylist() {
        val name = binding.playlistName.text.toString().trim()
        if (name.isEmpty()) return

        hasChanges = false

        val description = binding.playlistDescription.text.toString().trim()

        val coverPath = selectedImageUri?.toString() ?: ""

        viewModel.createPlaylist(name, description, coverPath)

        showCustomToast(
            requireContext(),
            getString(R.string.playlist_created, name)
        )

        findNavController().navigateUp()
    }

    private fun showExitDialog() {
        AlertDialog.Builder(requireContext(),R.style.CustomAlertDialogTheme)
            .setTitle(R.string.exit_playlist_creation_title)
            .setMessage(R.string.exit_playlist_creation_message)
            .setPositiveButton(R.string.finish) { _, _ ->
                findNavController().navigateUp()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun closeKeyboard() {
        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}