const int _kEnterEvent = 1;
const int _kExitEvent = 2;
enum GeofenceEvent {
  ENTER, EXIT
}

// Internal.
int geofenceEventToInt(GeofenceEvent e) {
  switch (e) {
    case GeofenceEvent.ENTER:
      return _kEnterEvent;
    case GeofenceEvent.EXIT:
      return _kExitEvent;
    default:
      throw UnimplementedError();
  }
}

// TODO(bkonyi): handle event masks
// Internal.
GeofenceEvent intToGeofenceEvent(int e) {
  switch (e) {
    case _kEnterEvent:
      return GeofenceEvent.ENTER;
    case _kExitEvent:
      return GeofenceEvent.EXIT;
    default:
      throw UnimplementedError();
  }
}