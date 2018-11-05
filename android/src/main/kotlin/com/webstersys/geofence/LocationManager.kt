package com.webstersys.geofence

import android.content.Context
import android.util.Log
import com.google.android.gms.location.*
import java.lang.Exception
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.LocationSettingsStatusCodes
import android.content.IntentSender
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import android.app.PendingIntent
import android.content.Intent
import com.webstersys.geofence.GeofenceConstant.Companion.CALLBACK_HANDLE_KEY
import com.webstersys.geofence.geofence.GeofenceBroadcastReceiver
import com.webstersys.geofence.geofence.GeofenceModel
import com.webstersys.geofence.geofence.GeofencingStore
import com.webstersys.geofence.location.LocationUpdatesBroadcastReceiver


class LocationManager(var context: Context) {
    private val TAG : String = LocationManager::class.java.canonicalName
    private var geofencingClient: GeofencingClient
    private var fusedLocationClient: FusedLocationProviderClient
    private var geofencingStore: GeofencingStore
    var callbackHandle : Long = 0
    private var geofenceList : ArrayList<Geofence> = arrayListOf()
    companion object {
        val GEOFENCE_ID : String = "webstersys_geofence_id"

    }


    init {
        this.geofencingClient = LocationServices.getGeofencingClient(context)
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        this.geofencingStore = GeofencingStore(context)
    }
    fun stopLocationMonitoring(){
        Log.i(TAG, "Removing location updates");
        fusedLocationClient.removeLocationUpdates(getLocationUpdatesBroadcastReceiverPendingIntent());
    }
    fun startMonitoringJob(){
        startLocationMonitoring()
        startGeofenceMonitoring()
        geofencingStore.setMonitoring(true)
    }
    fun stopMonitoringJob(){
        geofencingStore.setMonitoring(false)
        removeGeofenceFromStore()
        stopGeofenceMonitoring()
        stopLocationMonitoring()
    }
    fun startLocationMonitoring(){
        Log.d(TAG,"startLocationMonitoring called")
        try {
            val locationRequest = createLocationRequest()
            val locationSettingsRequest = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
            val settingsClient = LocationServices.getSettingsClient(context)
            settingsClient.checkLocationSettings(locationSettingsRequest).run {
                addOnSuccessListener {
                    Log.d(TAG, "All location settings are satisfied.");
                    fusedLocationClient.requestLocationUpdates(locationRequest,getLocationUpdatesBroadcastReceiverPendingIntent())

                }
                addOnFailureListener {
                    val errorMessage = it.message
                    Log.d(TAG, errorMessage)
                    val statusCode = (it as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            Log.d(TAG, "Location settings are not satisfied. Attempting to upgrade " + "location settings ")
                            try {
                                val rae = it as ResolvableApiException
                            } catch (sie: IntentSender.SendIntentException) {
                                Log.d(TAG, "PendingIntent unable to execute request.")
                            }
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage = "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings."
                            Log.e(TAG, errorMessage)
                        }
                    }
                    throw Exception("Location settings are not satisfied. Open Settings & change location settings")
                }
            }

            LocationServices.getFusedLocationProviderClient(context)
        }catch (securityException: SecurityException){
            Log.d(TAG,"Security Exception:"+securityException.message)
            throw Exception(securityException.message)
        }
        catch (ex: Exception){
            Log.d(TAG,ex.message)
            throw Exception(ex.message)
        }
    }
    fun stopGeofenceMonitoring(){
        Log.d(TAG,"Removing geofence")
        removeGeofenceFromStore()
        geofencingClient.removeGeofences(getGeofenceBroadcastReceiverPendingIntent())
    }
    fun addGeofenceToStore(geofence: GeofenceModel){
        geofencingStore.put(GEOFENCE_ID,geofence)
        Log.d(TAG,"geofence added to store")
    }
    fun getGeofeanceFromStore() : GeofenceModel?
    {
        Log.d(TAG,"geofence fetched from store")
        return geofencingStore.get(GEOFENCE_ID)
    }
    fun removeGeofenceFromStore(){
        geofencingStore.remove(GEOFENCE_ID)
        Log.d(TAG,"geofence removed from store")
    }
    fun startGeofenceMonitoring(){
        try {
           /* val geofence:Geofence = Geofence.Builder()
                .setRequestId("FirstGeofnce")
                .setCircularRegion(18.482477,73.797306,3000f)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setNotificationResponsiveness(1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()*/
            val geofence =  geofencingStore.get(GEOFENCE_ID)
            if(geofence==null){
                Log.d(TAG,"No geofence found in store")
                return
            }
            geofenceList.add(geofence.toGeofence())
            geofencingClient.addGeofences(getGeofencingRequest(), getGeofenceBroadcastReceiverPendingIntent()).run {
                addOnSuccessListener {
                    Log.d(TAG,"Geofence added successfully")
                }
                addOnFailureListener {
                    val errorMessage = it.message
                    Log.d(TAG, errorMessage)
                    throw Exception(errorMessage)
                }
            }
        }catch (securityException: SecurityException){
            Log.d(TAG,"Security Exception:"+securityException.message)
            throw Exception(securityException.message)
        }
        catch (ex:Exception){
            Log.d(TAG,ex.message)
            throw Exception(ex.message)
        }
    }
    protected fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofenceList)
        }.build()
    }

    protected fun createLocationRequest() : LocationRequest {
        val locationRequest = LocationRequest().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        return locationRequest
    }
    private fun getGeofenceBroadcastReceiverPendingIntent() : PendingIntent  {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        intent.putExtra(CALLBACK_HANDLE_KEY,callbackHandle)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getLocationUpdatesBroadcastReceiverPendingIntent(): PendingIntent {
        val intent = Intent(context, LocationUpdatesBroadcastReceiver::class.java)
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


}