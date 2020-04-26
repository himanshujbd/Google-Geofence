package com.googlegeofenceapi

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.google.android.gms.maps.model.LatLng

 class AppConstants {

    companion object {
        const val LOCATION_REQUEST_CODE = 101
        const val REMINDER_REQUEST_CODE = 201
        const val LAT_LNG = "LAT_LNG"
        const val ZOOM = "ZOOM"

        const val LOG_TAG = "GeoTrIntentService"

        private const val JOB_ID = 301

        const val PREFS_NAME = "ReminderRepository"
        const val REMINDERS = "REMINDERS"

        fun enqueueWork(context: Context, intent: Intent) {
            JobIntentService.enqueueWork(context, GeofenceTransitionService::class.java, JOB_ID,
                intent
            )
        }

        fun newIntentForMainActivity(context: Context, latLng: LatLng): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(LAT_LNG, latLng)
            return intent
        }

        fun newIntentForReminderActivity(context: Context, latLng: LatLng, zoom: Float): Intent {
            val intent = Intent(context, ReminderActivity::class.java)
            intent
                .putExtra(LAT_LNG, latLng)
                .putExtra(ZOOM, zoom)
            return intent
        }
    }
}