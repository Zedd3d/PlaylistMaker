package com.zeddikus.playlistmaker.ui.mediatec.fragment

import android.app.AlertDialog
import android.content.ContentResolver
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.zeddikus.playlistmaker.databinding.FragmentPlaylistBinding
import com.zeddikus.playlistmaker.ui.main.activity.MainActivity
import com.zeddikus.playlistmaker.ui.mediatec.view_model.PlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.UUID
import com.zeddikus.playlistmaker.R as R1


class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
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

    private fun fillingErrors(): Boolean {
        return binding.tiePlaylistnameEditText.text.toString().isEmpty()
    }

    private fun closeFragment(playlistSaved: Boolean) {
        if (playlistSaved) {
            Toast.makeText(
                requireContext(),
                "${getText(R1.string.playlist)} ${binding.tiePlaylistnameEditText.text.toString()} ${
                    getText(R1.string.created)
                }",
                Toast.LENGTH_SHORT
            ).show()
        }
        findNavController().popBackStack()
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

        viewModel.getState().observe(viewLifecycleOwner) { updateRender ->
            render()
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

    fun render() {
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