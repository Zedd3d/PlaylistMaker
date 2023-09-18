package com.zeddikus.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnSearch = findViewById<Button>(R.id.btn_search)
        val buttonClickListener: View.OnClickListener = object : View.OnClickListener { override fun onClick(v: View?) {
            if (v != null) {
                //pressedButton(v)
                startSearchActivity()
            }
        } }
        btnSearch.setOnClickListener(buttonClickListener)

        val btnMediatec = findViewById<Button>(R.id.btn_mediatec)
        btnMediatec.setOnClickListener {
            //pressedButton(it)
            val intent = Intent(this,MediatecActivity::class.java)
            startActivity(intent)
        }

        val btnSettings = findViewById<Button>(R.id.btn_settings)
        btnSettings.setOnClickListener {
            //pressedButton(it)
            val intent = Intent(this,SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startSearchActivity() {
        val intent = Intent(this,SearchActivity::class.java)
        startActivity(intent)
    }

//    fun pressedButton(v: View){
//        if (v is Button){
//            Toast.makeText(this@MainActivity, "Нажата кнопка ${findViewById<Button>(v.id).text.toString()}", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this@MainActivity, "Нажата кнопка ${v.id}", Toast.LENGTH_SHORT).show()
//        }
//
//    }
}