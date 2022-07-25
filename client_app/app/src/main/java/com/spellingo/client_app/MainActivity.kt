package com.spellingo.client_app

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
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

    private fun scheduleNotification(set: Boolean) {
        // Create intent to send notification
        val notifyIntent = Intent(this, NotificationReceiver::class.java)
        notifyIntent.putExtra("CHANNEL_ID", CHANNEL_ID)

        // Check if the alarm is already set to display notification daily
        if(PendingIntent.getBroadcast(this, REQUEST_CODE, notifyIntent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE) != null) {
            return
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            REQUEST_CODE,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        // Set notification to run daily
        if(set) {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
        else {
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun setNotification(sharedPreferences: SharedPreferences) {
        try {
            // Notification for Word of the Day
            val wotd = sharedPreferences.getBoolean("enable_wotd_mode", true)
            if(wotd) {
                createNotificationChannel()
            }
            scheduleNotification(wotd)
        }
        catch(e: Exception) {
            System.err.println(e.toString())
            System.err.println(e.printStackTrace())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)

        setNotification(sharedPreferences)

        sharedPreferences.registerOnSharedPreferenceChangeListener { spref, key ->
            if(key == "enable_wotd_mode") {
                setNotification(spref)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Catch notification's intent
        setIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        val extras = intent.extras
        if(extras != null) {
            // If we came from notification, navigate to game session to play Word of the Day
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