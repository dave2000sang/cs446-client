package com.spellingo.client_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        //TODO populate with word of the day stuff
        try {
            if(intent!!.extras == null) throw Exception("No channel id")
            val channelId = intent.extras!!.getString("CHANNEL_ID")!!
            val builder = NotificationCompat.Builder(context!!, channelId)
                .setSmallIcon(R.drawable.icon_bee)
                .setContentTitle("Word of the day")
                .setContentText("")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            with(NotificationManagerCompat.from(context!!)) {
                notify(0, builder.build())
            }
        }
        catch(e: Exception) {
            System.err.println(e.toString())
            System.err.println(e.printStackTrace())
        }
    }
}