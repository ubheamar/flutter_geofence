package com.webstersys.geofence

import android.Manifest

class GeofenceConstant {
    companion object {
        val CHANNEL_NAME : String = "geofence"
        val CHANNEL_BACKGROUND_NAME : String = "geofence_background"
        val GEOFENCE_INIT_METHOD : String = "initializeGeofence"
        val GEOFENCE_REGISTER_METHOD : String = "registerGeofence"
        val GEOFENCE_UNREGISTER_METHOD : String = "unregisterGeofence"
        val REQUIRED_PERMISSIONS = arrayListOf(Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION,
                                            Manifest.permission.WAKE_LOCK)
        val PERMISSION_DENIED_ERROR = "permission_denied"
        val REGISTER_GEOFENCE_ERROR = "register_geofence_error"


        val CALLBACK_HANDLE_KEY = "callback_handle"
        val CALLBACK_DISPATCHER_KEY = "callback_dispatch_handler"

        val GEOFENCE_JOB_ID = 55773322
        val NOTIFICATION_CHANNEL_ID = "geofence_notification_channel"
    }
}