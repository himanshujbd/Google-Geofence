
package com.googlegeofenceapi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build

import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*

fun vectorToBitmap(resources: Resources, @DrawableRes id: Int): BitmapDescriptor {
  val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
  val bitmap = Bitmap.createBitmap(vectorDrawable!!.intrinsicWidth,
      vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
  val canvas = Canvas(bitmap)
  vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
  vectorDrawable.draw(canvas)
  return BitmapDescriptorFactory.fromBitmap(bitmap)
}

fun showReminderInMap(context: Context,
                      map: GoogleMap,
                      myReminder: MyReminder) {
  if (myReminder.latLng != null) {
    val latLng = myReminder.latLng as LatLng
    val vectorToBitmap = vectorToBitmap(context.resources, R.drawable.ic_marker)
    val marker = map.addMarker(MarkerOptions().position(latLng).icon(vectorToBitmap))
    marker.tag = myReminder.id
    if (myReminder.radius != null) {
      val radius = myReminder.radius as Double
      map.addCircle(CircleOptions()
          .center(myReminder.latLng)
          .radius(radius)
          .strokeColor(ContextCompat.getColor(context, R.color.colorPrimary))
          .fillColor(ContextCompat.getColor(context, R.color.colorCircle)))
    }
  }
}

private const val NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel"

fun sendNotification(context: Context, message: String, latLng: LatLng) {
  val notificationManager = context
      .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
      && notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
    val name = context.getString(R.string.app_name)
    val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
        name,
        NotificationManager.IMPORTANCE_DEFAULT)

    notificationManager.createNotificationChannel(channel)
  }

  val intent = AppConstants.newIntentForMainActivity(context.applicationContext, latLng)

  val stackBuilder = TaskStackBuilder.create(context)
      .addParentStack(MainActivity::class.java)
      .addNextIntent(intent)
  val notificationPendingIntent = stackBuilder
      .getPendingIntent(getUniqueId(), PendingIntent.FLAG_UPDATE_CURRENT)

  val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
      .setSmallIcon(R.drawable.ic_launcher_background)
      .setContentTitle(message)
      .setContentIntent(notificationPendingIntent)
      .setAutoCancel(true)
      .build()

  notificationManager.notify(getUniqueId(), notification)
}

private fun getUniqueId() = ((System.currentTimeMillis() % 10000).toInt())