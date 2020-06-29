package com.phong.teamcnpm.teambuilding.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.google.gson.JsonObject
import com.phong.teamcnpm.teambuilding.BuildConfig
import com.phong.teamcnpm.teambuilding.R
import com.phong.teamcnpm.teambuilding.ui.main.MainActivity


class ObserveSocketService : Service() {
  private var mSocket: Socket? = null
  val NOTIFICATION_ID = (1..10000000).random()

  private val mEmitter = Emitter.Listener {
    Log.d("#### Emiter", "onemiter")
    val json = it[0] as JsonObject
    val message = json["data"]
    val notification = createNotification("TeamBuilding", message.toString())
    (getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.notify(
      NOTIFICATION_ID,
      notification
    )
  }

  override fun onCreate() {
    super.onCreate()
    createNotificationChannel()
    try {
      mSocket = IO.socket(BuildConfig.BASE_URL)
      mSocket?.connect()
      mSocket?.on("msgFromSever", mEmitter)
      Log.d("######", "create socket sussess")
    } catch (_: Exception) {
      Log.d("######", "create socket err")
    }
  }

  override fun onBind(p0: Intent?): IBinder? {
    TODO("Not yet implemented")
  }


  private fun createNotification(title: String, content: String): Notification {
    val intent = MainActivity.getInstance(this, true)
    val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
    return NotificationCompat.Builder(this, CHANNEL_ID)
      .setSmallIcon(R.mipmap.ic_launcher)
      .setContentTitle(title)
      .setContentText(content)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setContentIntent(pendingIntent)
      .setAutoCancel(true)
      .build()
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
      channel.description = CHANNEL_DESCRIPTION
      val notificationManager = getSystemService(NotificationManager::class.java)
      notificationManager.createNotificationChannel(channel)
    }
  }

  override fun onDestroy() {
    mSocket?.off("msgFromSever", mEmitter)
    mSocket?.disconnect()
    mSocket = null
    Log.d("######", "destroy socket sussess")
    super.onDestroy()
  }

  companion object {
    const val CHANNEL_ID = "OBSERVE_SOCKET_CHANNEL_ID"
    const val CHANNEL_NAME = "OBSERVE_SOCKET_CHANNEL_NAME"
    const val CHANNEL_DESCRIPTION = "OBSERVE_SOCKET_CHANNEL_DESCRIPTION"
  }
}