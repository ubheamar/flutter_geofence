

import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:geofence/geofence_constant.dart';
import 'package:geofence/geofence_event.dart';
import 'package:geofence/location.dart';

void callbackDispatcher() {
  const MethodChannel _backgroundChannel = MethodChannel(GeofenceConstant.CHANNEL_BACKGROUND_NAME);
  WidgetsFlutterBinding.ensureInitialized();

  _backgroundChannel.setMethodCallHandler((MethodCall call) async {
    print("Callback Dispatcher Invoked: ${call.arguments}");
    final List<dynamic> args = call.arguments;
    final Function callback = PluginUtilities.getCallbackFromHandle(CallbackHandle.fromRawHandle(args[0]));
    assert(callback != null);
    final locationList = args[1].cast<double>();
    final triggeringLocation = Location.fromList(locationList);
    final GeofenceEvent event = intToGeofenceEvent(args[2]);

    // 3.3. Invoke callback.
    callback(triggeringLocation, event);
  });
  print('GeofencingPlugin dispatcher started');
  _backgroundChannel.invokeMethod(GeofenceConstant.GEOFENCE_INIT_METHOD);
}