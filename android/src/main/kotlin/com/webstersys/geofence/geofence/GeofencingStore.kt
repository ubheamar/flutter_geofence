package com.webstersys.geofence.geofence

import android.R.id.edit
import android.content.Context
import android.content.SharedPreferences
import android.content.Context.MODE_PRIVATE
import android.support.annotation.NonNull
import com.webstersys.geofence.GeofenceConstant


class GeofencingStore(context: Context)  {

    private var preferences: SharedPreferences? = null

    init {
        preferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)

    }

    fun setPreferences(preferences: SharedPreferences) {
        this.preferences = preferences
    }

    fun put(id: String, geofenceModel: GeofenceModel) {
        val editor = preferences!!.edit()
        editor.putLong(getFieldKey(id, LATITUDE_ID), java.lang.Double.doubleToLongBits(geofenceModel.latitude))
        editor.putLong(getFieldKey(id, LONGITUDE_ID), java.lang.Double.doubleToLongBits(geofenceModel.longitude))
        editor.putFloat(getFieldKey(id, RADIUS_ID), geofenceModel.radius)
        editor.putInt(getFieldKey(id, TRANSITION_ID), geofenceModel.transition)
        editor.putLong(getFieldKey(id, EXPIRATION_ID), geofenceModel.expiration)
        editor.putInt(getFieldKey(id, LOITERING_DELAY_ID), geofenceModel.loiteringDelay)
        editor.apply()
    }
    fun isMonitoringShouldStart() : Boolean{
       return  preferences!!.getBoolean(LOCATION_MONITOR, false)
    }
    fun setMonitoring(shouldMonitor : Boolean ){
        val editor = preferences!!.edit()
        editor.putBoolean(LOCATION_MONITOR, shouldMonitor)
        editor.apply()
    }
    fun setCallbackDispatcher(dispacherId : Long){
        val editor = preferences!!.edit()
        editor.putLong(GeofenceConstant.CALLBACK_DISPATCHER_KEY, dispacherId)
        editor.apply()
    }
    fun getCallbackDispatcher() : Long{
        return  preferences!!.getLong(GeofenceConstant.CALLBACK_DISPATCHER_KEY, 0L)
    }
    operator fun get(id: String): GeofenceModel? {
        if (preferences != null && preferences!!.contains(getFieldKey(id, LATITUDE_ID)) && preferences!!.contains(
                getFieldKey(id, LONGITUDE_ID)
            )
        ) {
            val builder = GeofenceModel.Builder(id)
            builder.setLatitude(
                java.lang.Double.longBitsToDouble(
                    preferences!!.getLong(
                        getFieldKey(id, LATITUDE_ID),
                        0
                    )
                )
            )
            builder.setLongitude(
                java.lang.Double.longBitsToDouble(
                    preferences!!.getLong(
                        getFieldKey(id, LONGITUDE_ID),
                        0
                    )
                )
            )
            builder.setRadius(preferences!!.getFloat(getFieldKey(id, RADIUS_ID), 0f))
            builder.setTransition(preferences!!.getInt(getFieldKey(id, TRANSITION_ID), 0))
            builder.setExpiration(preferences!!.getLong(getFieldKey(id, EXPIRATION_ID), 0))
            builder.setLoiteringDelay(preferences!!.getInt(getFieldKey(id, LOITERING_DELAY_ID), 0))
            return builder.build()
        } else {
            return null
        }
    }

    fun remove(id: String) {
        val editor = preferences!!.edit()
        editor.remove(getFieldKey(id, LATITUDE_ID))
        editor.remove(getFieldKey(id, LONGITUDE_ID))
        editor.remove(getFieldKey(id, RADIUS_ID))
        editor.remove(getFieldKey(id, TRANSITION_ID))
        editor.remove(getFieldKey(id, EXPIRATION_ID))
        editor.remove(getFieldKey(id, LOITERING_DELAY_ID))
        editor.apply()
    }

    private fun getFieldKey(id: String, field: String): String {
        return PREFIX_ID + "_" + id + "_" + field
    }

    companion object {

        private val PREFERENCES_FILE = "GEOFENCING_STORE"
        private val PREFIX_ID = GeofencingStore::class.java.canonicalName + ".KEY"
        private val LATITUDE_ID = "LATITUDE"
        private val LONGITUDE_ID = "LONGITUDE"
        private val RADIUS_ID = "RADIUS"
        private val TRANSITION_ID = "TRANSITION"
        private val EXPIRATION_ID = "EXPIRATION"
        private val LOITERING_DELAY_ID = "LOITERING_DELAY"
        private val LOCATION_MONITOR = "LOCATION_MONITOR"
    }

}