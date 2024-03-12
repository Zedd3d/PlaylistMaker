package com.zeddikus.playlistmaker.ui.mediatec.fragment

import android.app.AlertDialog
import android.content.ContentResolver
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.zeddikus.playlistmaker.databinding.FragmentPlaylistSettingsBinding
import com.zeddikus.playlistmaker.domain.mediatec.playlists.models.PlaylistSettingsState
import com.zeddikus.playlistmaker.ui.main.activity.MainActivity
import com.zeddikus.playlistmaker.ui.mediatec.view_model.PlaylistSettingsViewModel
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.UUID
import com.zeddikus.playlistmaker.R as R1


class PlaylistSettingsFragment : Fragment() {
    private var _binding: FragmentPlaylistSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistSettingsViewModel by inject {

        val bundle: Bundle? = arguments
        val playlistId: Int? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle?.getInt(PlaylistFragment.PLAYLIST_DATA, 0)
            } else {
                bundle?.getInt(PlaylistFragment.PLAYLIST_DATA)
            }

        parametersOf(
            playlistId
        )
    }

    companion object {
        const val PLAYLIST_DATA = "PlaylistData"
        fun createArgs(playlistId: Int?): Bundle =
            bundleOf(PLAYLIST_DATA to playlistId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistSettingsBinding.inflate(inflater, container, false)
        activity?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListenersWatchersObservers()
        (activity as MainActivity).animateBottomNavigationView(View.GONE)
    }

    private fun setListenersWatchersObservers() {

        binding.createPlaylistButton.setOnClickListener {
            if (fillingErrors()) {
                binding.tilPlaylistdescTextInput.isErrorEnabled = true
                binding.tilPlaylistnameTextInput.error = getString(R1.string.need_fill)
            } else {
                savePlaylist()
            }
        }


        val simpleTextWatcherName = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.changeText()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.tiePlaylistnameEditText.addTextChangedListener(simpleTextWatcherName)

        val simpleTextWatcherDesc = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.changeText()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.tiePlaylistdescEditText.addTextChangedListener(simpleTextWatcherDesc)

        viewModel.getState().observe(viewLifecycleOwner) { state ->
            render(state)
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.ivPlaylist.setImageURI(uri)
                    val filename = saveImageToPrivateStorage(uri)
                    viewModel.saveFilenameCover(filename)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
        binding.ivPlaylist.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        viewModel.getCanGoBack().observe(viewLifecycleOwner) { canGoBack ->
            onBackPressedResult(canGoBack)
        }

        viewModel.getCloseFragment().observe(viewLifecycleOwner) { playlistCreated ->
            closeFragment(playlistCreated)
        }
    }

    private fun fillingErrors(): Boolean {
        return binding.tiePlaylistnameEditText.text.toString().isEmpty()
    }

    private fun closeFragment(playlistSaved: Boolean) {
        if (playlistSaved) {
            Toast.makeText(
                requireContext(),
                "${getText(R1.string.playlist)} ${binding.tiePlaylistnameEditText.text.toString()} ${
                    lastWord()
                }",
                Toast.LENGTH_SHORT
            ).show()
        }
        findNavController().popBackStack()
    }

    private fun lastWord(): String {
        return if (binding.tvToolbarText.text == getText(R1.string.edit)) {
            getText(R1.string.edited).toString()
        } else {
            getText(R1.string.created).toString()
        }
    }

    private fun savePlaylist() {
        viewModel.insertPlaylist(
            binding.tiePlaylistnameEditText.text.toString(),
            binding.tiePlaylistdescEditText.text.toString()
        )
    }

    private fun showDialog(messageText: String, positiveCreate: Boolean) {
        val dialogWindow = MaterialAlertDialogBuilder(requireActivity())
            .setNeutralButton(getString(R1.string.cancel)) { dialog, which ->
            }
            .setPositiveButton(getString(R1.string.complete)) { dialog, which ->
                if (positiveCreate) {
                    //Сценарий для подтверждения сохранения изменений
                    savePlaylist()
                } else {
                    closeFragment(false)
                }
            }.setBackground(requireContext().getDrawable(R1.drawable.btn_corners))
            .create()
        val dialogView = layoutInflater.inflate(R1.layout.alert_dialog, null)
        dialogView.findViewById<TextView>(R1.id.tvTextTitle).text =
            getString(R1.string.q_ending_create_playlist_title)
        dialogView.findViewById<TextView>(R1.id.tvTextMessage).text = messageText
        dialogWindow.setView(dialogView)
        dialogWindow.setOnShowListener {
            setValuesDialogWindow(dialogWindow)
        }

        dialogWindow.show()
    }

    private fun setValuesDialogWindow(dialogWindow: androidx.appcompat.app.AlertDialog) {
        val textColor = resources.getColor(R1.color.whiteDay_blackNight, requireContext().theme)
        val backgroundColor =
            resources.getColor(R1.color.blackDay_whiteNight, requireContext().theme)

        val btnPositive = dialogWindow.getButton(AlertDialog.BUTTON_POSITIVE)
        btnPositive.setTextColor(textColor)
        btnPositive.setBackgroundColor(backgroundColor)

        val btnCancel = dialogWindow.getButton(DialogInterface.BUTTON_NEUTRAL)
        btnCancel.setTextColor(textColor)
        btnCancel.setBackgroundColor(backgroundColor)

        dialogWindow.window?.decorView?.setBackgroundColor(backgroundColor)
        dialogWindow.window?.decorView?.setBackgroundResource(R1.drawable.alert_background)//setBackgroundColor(backgroundColor)
    }


    private fun onBackPressed() {
        viewModel.checkCanGoBack(
            binding.tiePlaylistnameEditText.text.toString().isNotEmpty(),
            binding.tiePlaylistdescEditText.text.toString().isNotEmpty()
        )
    }

    private fun onBackPressedResult(canGoBack: Boolean) {
        if (canGoBack) {
            closeFragment(false)
        } else {
            showDialog(getString(R1.string.q_message_confirm_exit_without_create_playlist), false)
        }
    }

    fun getMimeType(uri: Uri): String {
        var mimeType = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val cr: ContentResolver = requireActivity().contentResolver
            cr.getType(uri).toString()
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
                uri
                    .toString()
            )
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.lowercase(Locale.getDefault())
            ).toString()
        }

        mimeType = mimeType.split("/").last()

        return mimeType
    }

    private fun saveImageToPrivateStorage(uri: Uri): String {
        //создаём экземпляр класса File, который указывает на нужный каталог
        val filePath = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "album_playlist_covers"
        )
        //создаем каталог, если он не создан
        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val fileName = "${UUID.randomUUID()}.${getMimeType(uri)}"
        val file = File(filePath, fileName)
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return fileName
    }

    fun render(state: PlaylistSettingsState) {

        when (state) {
            is PlaylistSettingsState.PlaylistData -> {
                if (state.playlist.playlistCover.isNotEmpty()) {
                    val filePath = File(
                        requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "album_playlist_covers"
                    )
                    val file = File(filePath, state.playlist.playlistCover)
                    binding.ivPlaylist.setImageURI(file.toUri())
                }
                binding.tiePlaylistnameEditText.setText(state.playlist.playlistName)
                binding.tiePlaylistdescEditText.setText(state.playlist.playlistDescription)
                binding.createPlaylistButton.setText(R1.string.save)
                binding.tvToolbarText.setText(R1.string.edit)
            }

            else -> {}
        }

        //сделал для себя изменение цвета двумя разными способами.
        // 1 с использованием заранее заготовленного selector
        // 2 с изменением самого selector
        val nameFilled =
            updateEditTextView(binding.tiePlaylistnameEditText, binding.tilPlaylistnameTextInput)
        updateEditTextView(binding.tiePlaylistdescEditText, binding.tilPlaylistdescTextInput)

        binding.createPlaylistButton.isSelected = nameFilled
        binding.tilPlaylistnameTextInput.isErrorEnabled = false
    }

    fun updateEditTextView(
        editTextField: TextInputEditText,
        textInputLayout: TextInputLayout
    ): Boolean {
        var colorStateId = 0
        var result = false
        if (editTextField.text.toString().isEmpty()) {
            colorStateId = R1.color.text_input_layout_outline

        } else {
            //Ещё кусочек для себя. Получение ресурса по имени. Это не правильно на Проде в коммерческой разработке. Я знаю.
//            colorSetId = resources.getIdentifier("text_input_layout_outline_filled", "color",
//                requireContext().getPackageName())
            colorStateId = R1.color.text_input_layout_outline_filled
            result = true
        }
        val colorState = requireContext().getColorStateList(colorStateId)
        textInputLayout.setBoxStrokeColorStateList(colorState)
        textInputLayout.defaultHintTextColor = colorState

        return result
    }


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}