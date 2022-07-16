package com.spellingo.client_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
            val activityIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val mainPendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_IMMUTABLE)
            val builder = NotificationCompat.Builder(context!!, channelId)
                .setSmallIcon(R.drawable.icon_bee)
                .setContentTitle("Word of the Day")
                .setContentText("Play today's word of the day now!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(mainPendingIntent)
                .setAutoCancel(true)
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