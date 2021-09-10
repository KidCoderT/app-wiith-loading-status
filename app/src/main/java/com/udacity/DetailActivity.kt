package com.udacity

import android.app.DownloadManager
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*


class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        filename_text.text = intent.getStringExtra("filename")
        status_text.text = intent.getStringExtra("downloadStatus")
        when (status_text.text) {
            "Success" -> status_text.setTextColor(Color.GREEN)
            else -> status_text.setTextColor(Color.RED)
        }
        ok_button.setOnClickListener {
            onBackPressed()
        }
    }

}
