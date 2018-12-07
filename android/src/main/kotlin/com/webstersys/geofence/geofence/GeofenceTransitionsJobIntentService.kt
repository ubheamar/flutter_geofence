package com.webstersys.geofence.geofence

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import android.text.TextUtils
import android.os.Build
import android.graphics.BitmapFactory
import android.app.PendingIntent
import android.app.NotificationManager
import android.app.NotificationChannel
import android.graphics.Color
import android.media.RingtoneManager
import android.support.v4.app.JobIntentService
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import com.webstersys.geofence.GeofenceConstant
import com.webstersys.geofence.GeofenceConstant.Companion.CALLBACK_HANDLE_KEY
import com.webstersys.geofence.GeofenceConstant.Companion.NOTIFICATION_CHANNEL_ID
import com.webstersys.geofence.HeadlessJobService

import com.webstersys.geofence.R
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry
import io.flutter.view.FlutterNativeView
import io.flutter.view.FlutterCallbackInformation
import io.flutter.view.FlutterMain
import io.flutter.view.FlutterRunArguments
import java.util.*


class GeofenceTransitionsJobIntentService : HeadlessJobService(GeofenceConstant.CHANNEL_BACKGROUND_NAME) {
    private val queue = ArrayDeque<List<Any>>()

    companion object {
        private val TAG:String = GeofenceTransitionsJobIntentService::class.java.canonicalName

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, GeofenceTransitionsJobIntentService::class.java, GeofenceConstant.GEOFENCE_JOB_ID, intent)
        }
        fun setPluginRegistrant(callback: PluginRegistry.PluginRegistrantCallback) {
            sPluginRegistrantCallback = callback
        }
    }


    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when(call.method) {
            GeofenceConstant.GEOFENCE_INIT_METHOD->{
                synchronized(sServiceStarted){
                    while (!queue.isEmpty()) {
                        mBackgroundChannel.invokeMethod("", queue.remove())
                    }
                    sServiceStarted.set(true)
                }
            }
            else -> result.notImplemented()
        }
        result.success(null)
    }


    override fun onHandleWork(intent: Intent) {
        val callbackHandle = intent.getLongExtra(CALLBACK_HANDLE_KEY, 0)
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceErrorMessages.getErrorString(
                this,
                geofencingEvent.errorCode
            )
            Log.e(TAG, errorMessage)
            return
        }
        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            val listOfAllTriggeredGeofence = geofencingEvent.triggeringGeofences

            // Get the transition details as a String.
            val geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition, listOfAllTriggeredGeofence)

            Log.i(TAG, geofenceTransitionDetails)
            // Send notification and log the transition details.
            //sendNotification(geofenceTransitionDetails)
            //Dart callback
            val location = geofencingEvent.triggeringLocation
            val locationList = listOf(location.latitude, location.longitude)
            val geofenceUpdateList = listOf(callbackHandle, locationList, geofenceTransition)
            synchronized(sServiceStarted) {
                if (!sServiceStarted.get()) {
                    queue.add(geofenceUpdateList)
                } else {
                    mBackgroundChannel.invokeMethod("", geofenceUpdateList)
                }
            }
        } else {
            // Log the error.
            Log.e(TAG, getString(R.string.geofence_transition_invalid_type, geofenceTransition))
        }
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private fun getGeofenceTransitionDetails(
        geofenceTransition: Int,
        triggeringGeofences: List<Geofence>
    ): String {

        val geofenceTransitionString = getTransitionString(geofenceTransition)

        // Get the Ids of each geofence that was triggered.
        val triggeringGeofencesIdsList = arrayListOf<String>()
        for (geofence in triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.requestId)
        }
        val triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList)

        return "$geofenceTransitionString: $triggeringGeofencesIdsString"
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private fun sendNotification(notificationDetails: String) {
        // Get an instance of the Notification manager
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            // Create the channel for the notification
            val mChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel)
        }

        // Create an explicit content Intent that starts the main Activity.
       // val notificationIntent = Intent(applicationContext, MainActivity::class.java)
       // val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        // Construct a task stack.
       // val stackBuilder = TaskStackBuilder.create(this)

        // Add the main Activity to the task stack as the parent.
      //  stackBuilder.addParentStack(MainActivity::class.java)

        // Push the content Intent onto the stack.
       // stackBuilder.addNextIntent(notificationIntent)

        // Get a PendingIntent containing the entire back stack.
       // val notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        // Get a notification builder that's compatible with platform versions >= 4
        val builder = NotificationCompat.Builder(this)
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        // Define the notification settings.
        builder.setSmallIcon(R.drawable.ic_touch_app_black_24dp)
            // In a real app, you may want to use a library like Volley
            // to decode the Bitmap.
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.ic_touch_app_black_24dp
                )
            )
            .setColor(Color.RED)
            .setContentTitle(notificationDetails)
            .setContentText(getString(R.string.geofence_transition_notification_text))
            //.setContentIntent(notificationPendingIntent)

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NOTIFICATION_CHANNEL_ID) // Channel ID
        }

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true)

        // Issue the notification
        mNotificationManager.notify(0, builder.build())
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private fun getTransitionString(transitionType: Int): String {
        when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> return getString(R.string.geofence_transition_entered)
            Geofence.GEOFENCE_TRANSITION_EXIT -> return getString(R.string.geofence_transition_exited)
            else -> return getString(R.string.unknown_geofence_transition)
        }
    }



}