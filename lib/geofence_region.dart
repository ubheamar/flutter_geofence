import 'package:geofence/geofence_event.dart';
import 'package:geofence/location.dart';

/// A circular region which represents a geofence.
class GeofenceRegion {


  /// The location of the geofence.
  final Location location;

  /// The radius around `location` that will be considered part of the geofence.
  final double radius;


  GeofenceRegion(this.location, this.radius);

  List<dynamic> toArgs() {

    final List<dynamic> args = <dynamic>[
      location.latitude,
      location.longitude,
      radius
    ];
    return args;
  }


}