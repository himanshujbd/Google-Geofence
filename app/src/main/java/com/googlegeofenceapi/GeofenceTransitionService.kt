package com.googlegeofenceapi

import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceTransitionService : JobIntentService() {

    lateinit var repositoryMy: MyReminderRepository

    override fun onHandleWork(intent: Intent) {

        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        repositoryMy = MyReminderRepository(this)

        if (geofencingEvent.hasError()) {
            Log.e(AppConstants.LOG_TAG, "An error occured")
            return
        }
        handleEvent(geofencingEvent)
    }

    private fun handleEvent(event: GeofencingEvent) {

        if (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            val reminder = getFirstReminder(event.triggeringGeofences)
            val message = reminder?.message
            val latLng = reminder?.latLng
            if (message != null && latLng != null) {
                sendNotification(this, message, latLng)
            }
        }
    }

    private fun getFirstReminder(triggeringGeofences: List<Geofence>): MyReminder? {
        val firstGeofence = triggeringGeofences[0]
        return repositoryMy.get(firstGeofence.requestId)
    }
}
