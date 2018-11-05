package com.webstersys.geofence

import android.Manifest
import android.os.Build
import android.util.Log
import android.content.Context
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.webstersys.geofence.GeofenceConstant.Companion.PERMISSION_DENIED_ERROR
import com.webstersys.geofence.GeofenceConstant.Companion.REGISTER_GEOFENCE_ERROR
import com.webstersys.geofence.GeofenceConstant.Companion.REQUIRED_PERMISSIONS
import com.webstersys.geofence.geofence.GeofenceModel
import com.webstersys.geofence.geofence.GeofencingStore
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.lang.Exception

class GeofencePlugin(context: Context, activity: Activity?): MethodCallHandler {

  private var geofencingStore : GeofencingStore = GeofencingStore(context)

  private val mContext = context
  private val mActivity = activity
  private val TAG : String = GeofencePlugin.javaClass.canonicalName;
  private val locationManager  = LocationManager(context)

  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar): Unit {
      val channel = MethodChannel(registrar.messenger(), GeofenceConstant.CHANNEL_NAME)
      channel.setMethodCallHandler(GeofencePlugin(registrar.context(), registrar.activity()))
    }
  }

  override fun onMethodCall(call: MethodCall, result: Result): Unit {

    val args = call.arguments() as? ArrayList<*>
    if(call.method.equals(GeofenceConstant.GEOFENCE_INIT_METHOD)){
      Log.d(TAG,"Geofence initializing");
      val callbackHandle = args!![0] as Long
      geofencingStore.setCallbackDispatcher(callbackHandle)
      Log.d(TAG,"Callback dispatcher register successfully with id : "+callbackHandle)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        Dexter.withActivity(mActivity)
                .withPermissions(REQUIRED_PERMISSIONS)
                .withListener(object : MultiplePermissionsListener{
                  override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if(report!=null && report.areAllPermissionsGranted()){
                      Log.d(TAG,"Geofence initialized");
                      result.success(true);
                    }else{
                      result.error(PERMISSION_DENIED_ERROR,"All permission should be granted to work location functionality",null)
                    }
                  }
                  override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                      token?.continuePermissionRequest();
                  }
                })
                .check()
      }
    }
    else if(call.method.equals(GeofenceConstant.GEOFENCE_REGISTER_METHOD)){
      registerGeofence(args, result)
    }
    else if(call.method.equals(GeofenceConstant.GEOFENCE_UNREGISTER_METHOD)){
      locationManager.stopMonitoringJob()
      result.success(true)
    }
    else if(call.method.equals(GeofenceConstant.GEOFENCE_ENABLED_CHECK_METHOD)){
      result.success(geofencingStore.isMonitoringShouldStart())
    }
    else {
      result.notImplemented()
    }
  }

  private fun registerGeofence(args: ArrayList<*>?, result: Result) {
    try {
      Log.d(TAG,"Start Geofence called")
      val callbackHandle = args!![0] as Long
      val lat = args[1] as Double
      val long = args[2] as Double
      val radius = (args[3] as Double).toFloat()
      val geofenceModel   = GeofenceModel.Builder(LocationManager.GEOFENCE_ID)
              .setLatitude(lat)
              .setLongitude(long)
              .setRadius(radius)
              .setExpiration(Geofence.NEVER_EXPIRE)
              .setTransition(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
              .build()
      locationManager.addGeofenceToStore(geofenceModel)
      locationManager.callbackHandle = callbackHandle
      locationManager.startMonitoringJob()
      result.success(true)
    }catch (ex:Exception){
      result.error(REGISTER_GEOFENCE_ERROR, ex.message,null)
    }
  }


}
