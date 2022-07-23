package com.alexandruleonte.demo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alexandruleonte.demo.gallery.PhotoGalleryFragment
import com.alexandruleonte.demo.gallery.PreferencesRepository

class GalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        PreferencesRepository.initialize(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(com.alexandruleonte.demo.gallery.NOTIFICATION_CHANNEL_ID, name, importance)
            val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, GalleryActivity::class.java)
        }
    }
}