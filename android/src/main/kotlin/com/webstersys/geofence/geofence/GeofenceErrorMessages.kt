package com.webstersys.geofence.geofence

import android.content.Context
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.common.api.ApiException
import com.webstersys.geofence.R


/**
 * Geofence error codes mapped to error messages.
 */
internal object GeofenceErrorMessages {

    /**
     * Returns the error string for a geofencing exception.
     */
    fun getErrorString(context: Context, e: Exception): String {
        return if (e is ApiException) {
            getErrorString(context, e.statusCode)
        } else {
            context.getResources().getString(R.string.unknown_geofence_error)
        }
    }

    /**
     * Returns the error string for a geofencing error code.
     */
    fun getErrorString(context: Context, errorCode: Int): String {
        val mResources = context.getResources()
        when (errorCode) {
            GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> return mResources.getString(R.string.geofence_not_available)
            GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> return mResources.getString(R.string.geofence_too_many_geofences)
            GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> return mResources.getString(R.string.geofence_too_many_pending_intents)
            else -> return mResources.getString(R.string.unknown_geofence_error)
        }
    }
}
/**
 * Prevents instantiation.
 */