#import "GeofencePlugin.h"
#import <geofence/geofence-Swift.h>

@implementation GeofencePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftGeofencePlugin registerWithRegistrar:registrar];
}
@end
