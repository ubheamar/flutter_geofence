

class Location {
  final double latitude;
  final double longitude;

  const Location(this.latitude, this.longitude);
  Location.fromList(List<double> l)
      : assert(l.length == 2),
        latitude = l[0],
        longitude = l[1];

  @override
  String toString() => '($latitude, $longitude)';
}