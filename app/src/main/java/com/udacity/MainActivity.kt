package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private var filename = ""
    private var url =  ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        radio_group.setOnCheckedChangeListener { _, checkedId ->
            filename = when(checkedId) {
                R.id.glide_download -> "Glide - Image Loading Library by BumpTech"
                R.id.load_app_download -> "LoadApp - Current repository by Udacity"
                R.id.retrofit_download -> "Retrofit - Type-safe HTTP client for Android and Java by Square, Inc"
                else -> ""
            }
            url = when(checkedId) {
                R.id.glide_download -> "https://github.com/bumptech/glide"
                R.id.load_app_download -> "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
                R.id.retrofit_download -> "https://github.com/square/retrofit"
                else -> ""
            }
        }

        custom_loading_button.setOnClickListener {
            Log.i("Sup", "$filename $url")
            download(url)
        }

        createChannel(
            getString(R.string.download_notification_channel_id),
            getString(R.string.download_notification_channel_name)
        )
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            custom_loading_button.hasCompletedDownload()
            if (id == downloadID) {
                sendNotification(applicationContext, "Success")
            } else {
                sendNotification(applicationContext, "Failed")
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

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download Finished"

            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun sendNotification(context: Context, status: String) {
        val notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelNotifications()

        notificationManager.sendNotification(
            context.getText(R.string.notification_description).toString(),
            filename,
            status,
            context
        )
    }
}
