import 'dart:async';
import 'dart:ui';

import 'package:flutter/services.dart';
import 'package:geofence/geofence_constant.dart';
import 'package:geofence/geofence_event.dart';
import 'package:geofence/geofence_region.dart';
import 'package:geofence/location.dart';
import 'package:geofence/callback_dispatcher.dart';

class Geofence {
  static const MethodChannel _channel =
      const MethodChannel(GeofenceConstant.CHANNEL_NAME);

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<bool> initialize() async {
    final List<dynamic> args = <dynamic>[
      PluginUtilities.getCallbackHandle(callbackDispatcher).toRawHandle()
    ];
    return await _channel.invokeMethod(
        GeofenceConstant.GEOFENCE_INIT_METHOD, args);
  }

  static Future<bool> registerGeofence(GeofenceRegion region,
      void Function(Location location, GeofenceEvent event) callback) async {
    final List<dynamic> args = <dynamic>[
      PluginUtilities.getCallbackHandle(callback).toRawHandle()
    ];
    args.addAll(region.toArgs());
    return await _channel.invokeMethod(
        GeofenceConstant.GEOFENCE_REGISTER_METHOD, args);
  }

  static Future<bool> unRegisterGeofence() async {
    return await _channel
        .invokeMethod(GeofenceConstant.GEOFENCE_UNREGISTER_METHOD);
  }
}
