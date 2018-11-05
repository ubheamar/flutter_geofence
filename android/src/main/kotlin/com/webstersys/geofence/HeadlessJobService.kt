package com.webstersys.geofence

import android.content.Context
import android.support.v4.app.JobIntentService
import android.util.Log
import com.webstersys.geofence.geofence.GeofencingStore
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry
import io.flutter.view.FlutterCallbackInformation
import io.flutter.view.FlutterMain
import io.flutter.view.FlutterNativeView
import io.flutter.view.FlutterRunArguments
import java.util.concurrent.atomic.AtomicBoolean

abstract class HeadlessJobService(val backgroundChannelName: String) : JobIntentService(), MethodChannel.MethodCallHandler {
    val TAG = HeadlessJobService::class.java.canonicalName
    lateinit var mContext: Context
    lateinit var mBackgroundChannel: MethodChannel
    lateinit var geofencingStore : GeofencingStore
    companion object {
        val sServiceStarted = AtomicBoolean(false)
        var sBackgroundFlutterView: FlutterNativeView? = null
        lateinit var sPluginRegistrantCallback: PluginRegistry.PluginRegistrantCallback



    }

    override fun onCreate() {
        super.onCreate()
        startHeadlessService(this)
    }

    protected fun startHeadlessService(context: Context){
        synchronized(sServiceStarted){
            mContext = context
            geofencingStore = GeofencingStore(mContext)
            if (sBackgroundFlutterView == null) {
                val callbackDispatcher = geofencingStore.getCallbackDispatcher()
                val callbackInfo = FlutterCallbackInformation.lookupCallbackInformation(callbackDispatcher)
                if (callbackInfo == null) {
                    Log.e(TAG, "Fatal: failed to find callback")
                    return
                }
                Log.i(TAG, "Starting Geofencing Service...")
                sBackgroundFlutterView = FlutterNativeView(mContext, true)
                val registry = sBackgroundFlutterView!!.pluginRegistry
                sPluginRegistrantCallback.registerWith(registry)
                val args = FlutterRunArguments()
                args.bundlePath = FlutterMain.findAppBundlePath(mContext)
                args.entrypoint = callbackInfo.callbackName
                args.libraryPath = callbackInfo.callbackLibraryPath
                sBackgroundFlutterView!!.runFromBundle(args)
            }
        }
        mBackgroundChannel = MethodChannel(sBackgroundFlutterView, backgroundChannelName)
        mBackgroundChannel.setMethodCallHandler(this)
    }
}