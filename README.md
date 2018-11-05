# geofence

This plugin help get geofence notification 

## Getting Started

### Android
Add the following lines to your AndroidManifest.xml to register the background service for geofencing:

```xml
<receiver   android:name="com.webstersys.geofence.geofence.GeofenceBroadcastReceiver"
            android:enabled="true"
            android:exported="true" />
<service android:name="com.webstersys.geofence.geofence.GeofenceTransitionsJobIntentService" android:enabled="true" android:exported="true"
            android:permission="android.permission.BIND_JOB_SERVICE"/>
<receiver android:name="com.webstersys.geofence.BootDeviceReceiver">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED"/>
            </intent-filter>
</receiver>
```

Also request the correct permissions for geofencing:
```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.WAKE_LOCK"/>

```

Finally, create either Application.kt or Application.java in the same directory as MainActivity.

For Application.kt, use the following:
```kotlin
class Application : FlutterApplication(), PluginRegistrantCallback {
  override fun onCreate() {
    super.onCreate();
    GeofenceTransitionsJobIntentService.setPluginRegistrant(this)
  }

  override fun registerWith(registry: PluginRegistry) {
    GeneratedPluginRegistrant.registerWith(registry);
  }
}
```

For Application.java, use the following:
```java
public class Application extends FlutterApplication implements PluginRegistrantCallback {
  @Override
  public void onCreate() {
    super.onCreate();
     GeofenceTransitionsJobIntentService.setPluginRegistrant(this);
  }

  @Override
  public void registerWith(PluginRegistry registry) {
    GeneratedPluginRegistrant.registerWith(registry);
  }
}
```

Which must also be referenced in AndroidManifest.xml:
```xml
   <application
        android:name=".Application" />
```