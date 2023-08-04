package com.zeddikus.playlistmaker


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView

class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val vTextSearch = findViewById<EditText>(R.id.vTextSearch)
        val clearButton = findViewById<ImageView>(R.id.btnSearchClear)
        clearButton.setOnClickListener{clearSearchText(clearButton,vTextSearch)}
        val btnBack = findViewById<ImageButton>(R.id.imgBtnBack)
        btnBack.setOnClickListener{finish()}

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        vTextSearch.addTextChangedListener(simpleTextWatcher)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT,findViewById<EditText>(R.id.vTextSearch).text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        findViewById<EditText>(R.id.vTextSearch).setText(savedInstanceState.getString(SEARCH_TEXT))
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
    private fun clearSearchText(btnClose: ImageView,editField: EditText) {
        editField.setText("")
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(`editField`.windowToken, 0)
        btnClose.visibility = View.GONE

    }

}


