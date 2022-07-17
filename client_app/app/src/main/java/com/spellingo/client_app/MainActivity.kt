package com.spellingo.client_app

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.preference.PreferenceManager
import com.spellingo.client_app.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val CHANNEL_ID = "wordday"
    private val REQUEST_CODE = 5

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleNotification() {
        val notifyIntent = Intent(this, NotificationReceiver::class.java)
        notifyIntent.putExtra("CHANNEL_ID", CHANNEL_ID)

        if(PendingIntent.getBroadcast(this, REQUEST_CODE, notifyIntent, PendingIntent.FLAG_NO_CREATE) != null) {
            println("DEBUG Alarm already set")
            return
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            REQUEST_CODE,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)

        try {
            createNotificationChannel()
            scheduleNotification()
        }
        catch(e: Exception) {
            System.err.println(e.toString())
            System.err.println(e.printStackTrace())
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        val extras = intent.extras
        if(extras != null) {
            if(extras.getBoolean("NOTIFICATION")) {
                val bundle = Bundle()
                bundle.putBoolean("wotd", true)
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
                val navController = navHostFragment.navController
                navController.navigate(R.id.fragment_game_session, bundle)
            }
        }
    }
}