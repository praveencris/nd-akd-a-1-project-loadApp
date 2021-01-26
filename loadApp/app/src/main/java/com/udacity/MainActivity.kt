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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        val notificationManager: NotificationManager =
            ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
            ) as NotificationManager

        binding.contentMain.customButton.setOnClickListener {
            (it as LoadingButton).setState(ButtonState.Clicked)
            notificationManager.cancelAllNotification()
            when (binding.contentMain.radioGroup.checkedRadioButtonId) {
                R.id.radioButtonGlide -> download(URL_GLIDE)
                R.id.radioButtonLoadApp -> download(URL_LOAD_APP)
                R.id.radioButtonRetrofit -> download(URL_RETROFIT)
                else -> Toast.makeText(
                    this@MainActivity,
                    R.string.select_file_to_download,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        createChannel(
            getString(R.string.notification_channel_id), getString(
                R.string.channel_name
            )
        )
    }

    private fun getMessageAndContentText(): Pair<String, String> {
        return when (binding.contentMain.radioGroup.checkedRadioButtonId) {
            R.id.radioButtonGlide -> Pair(
                getString(R.string.glide_library_desc),
                getString(R.string.notification_description, 1)
            )
            R.id.radioButtonLoadApp -> Pair(
                getString(R.string.load_app_desc),
                getString(R.string.notification_description, 2)
            )
            R.id.radioButtonRetrofit -> Pair(
                getString(R.string.retrofit_desc),
                getString(R.string.notification_description, 3)
            )
            else -> Pair("", "")
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val messageAndContentTextPair=getMessageAndContentText()
            binding.contentMain.customButton.setState(ButtonState.Completed)

            if (id == downloadID) {
                val notificationManager: NotificationManager = ContextCompat.getSystemService(
                    context!!,
                    NotificationManager::class.java
                ) as NotificationManager
                notificationManager.sendNotification(
                    messageAndContentTextPair.second,Pair(messageAndContentTextPair.first,getString(R.string.success)), context,)
            } else {
                val notificationManager: NotificationManager = ContextCompat.getSystemService(
                    context!!,
                    NotificationManager::class.java
                ) as NotificationManager
                notificationManager.sendNotification(
                    messageAndContentTextPair.second,Pair(messageAndContentTextPair.first,getString(R.string.failed)), context,)
            }
        }
    }

    private fun download(url: String) {
        binding.contentMain.customButton.setState(ButtonState.Loading)
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val URL_GLIDE =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val URL_LOAD_APP =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_RETROFIT =
            "https://github.com/square/retrofit/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }


    private fun createChannel(channelId: String, channelName: String) {
        // DONE: Step 1.6 START create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.apply {
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                description = "File downloading status"
            }

            val notificationManager =
                ContextCompat.getSystemService(this, NotificationManager::class.java)
                        as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }


    }
}
