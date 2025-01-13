package dev.drone_tracking;
import org.locationtech.spatial4j.shape.Point;
public record PointToRouteMetrics(double minDistance, Point nearestPoint, boolean nearestPointIsSegmentEndPoint) {

}
