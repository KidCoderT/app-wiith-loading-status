package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_loading_button.setOnClickListener {
            val url = when(radio_group.checkedRadioButtonId) {
                2131230865 -> "https://github.com/bumptech/glide"
                2131230896 -> "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
                2131230934 -> "https://github.com/square/retrofit"
                else -> ""
            }
            download(url)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                custom_loading_button.hasCompletedDownload()
            }
        }
    }

    private fun download(URL: String) {
        if (URL.isNotBlank()) {
            val request =
                DownloadManager.Request(Uri.parse(URL))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
            custom_loading_button.startDownload()
        } else {
            Toast.makeText(applicationContext, "Please select the file download", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val CHANNEL_ID = "channelId"
    }

}
