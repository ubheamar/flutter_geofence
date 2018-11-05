package com.webstersys.geofence

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.webstersys.geofence.geofence.GeofenceTransitionsJobIntentService
import com.webstersys.geofence.geofence.GeofencingStore

class BootDeviceReceiver : BroadcastReceiver() {
    private val TAG : String = BootDeviceReceiver::class.java.canonicalName
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent!=null && context!=null){
            if(Intent.ACTION_BOOT_COMPLETED.equals(intent.action)){
                Log.d(TAG,"StartUpBootReceiver BOOT_COMPLETED")
                val geofencingStore =  GeofencingStore(context)
                if(geofencingStore.isMonitoringShouldStart()) {
                    val locationManager = LocationManager(context)
                    locationManager.startMonitoringJob()
                }
            }
        }

    }
}