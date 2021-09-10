package com.udacity

import android.app.DownloadManager
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val cursor: Cursor =
            downloadManager.query(DownloadManager.Query().setFilterById(MainActivity.globalDownloadId))
    }

}
