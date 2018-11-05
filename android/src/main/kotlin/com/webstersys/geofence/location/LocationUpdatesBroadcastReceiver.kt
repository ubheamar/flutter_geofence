package com.webstersys.geofence.location

import com.google.android.gms.location.LocationResult
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log


/**
 * Receiver for handling location updates.
 *
 * For apps targeting API level O
 * [android.app.PendingIntent.getBroadcast] should be used when
 * requesting location updates. Due to limits on background services,
 * [android.app.PendingIntent.getService] should not be used.
 *
 * Note: Apps running on "O" devices (regardless of targetSdkVersion) may receive updates
 * less frequently than the interval specified in the
 * [com.google.android.gms.location.LocationRequest] when the app is no longer in the
 * foreground.
 */
class LocationUpdatesBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_PROCESS_UPDATES == action) {
                val result = LocationResult.extractResult(intent)
                if (result != null) {
                    val locations = result.locations
                   /* Utils.setLocationUpdatesResult(context, locations)
                    Utils.sendNotification(context, Utils.getLocationResultTitle(context, locations))
                    Log.i(TAG, Utils.getLocationUpdatesResult(context))*/
                }
            }
        }
    }

    companion object {
        private val TAG = LocationUpdatesBroadcastReceiver::class.java.canonicalName
        internal val ACTION_PROCESS_UPDATES = "com.google.android.gms.location.sample.locationupdatespendingintent.action" + ".PROCESS_UPDATES"
    }
}