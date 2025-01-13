package dev.drone_tracking;
import java.util.Iterator;
import java.util.List;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.operation.distance.DistanceOp;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.geom.Coordinate;
import static dev.drone_tracking.Utils.*;

public class RouteSegment {
  
    LineString currentSegment;
    public RouteSegment(LineString segment) {
        currentSegment = segment;
    }

    public PointToRouteMetrics getNearestPointAndDistance(org.locationtech.spatial4j.shape.Point point) {
        Point p = geometryFactory.createPoint(convertFromWG84ToWebMercator(point));
        DistanceOp distanceOp = new DistanceOp(currentSegment, p);
        double minDistance = distanceOp.distance();
        Coordinate nearestCoordinate = distanceOp.nearestPoints()[0];
        org.locationtech.spatial4j.shape.Point nearestPoint = convertFromWebMercatorToWG84(nearestCoordinate);
        return new PointToRouteMetrics(minDistance,nearestPoint,isTwoPointsEqial(currentSegment.getCoordinateN(1), nearestCoordinate));
    }

    public double getAzimithToTheEndPointOfCurrentSegment(org.locationtech.spatial4j.shape.Point point) {
        double angleRadians = Angle.angle(convertFromWG84ToWebMercator(point), currentSegment.getCoordinateN(1));
        double angleDegrees = Math.toDegrees(angleRadians);
        double cwAngle = (360 - angleDegrees) % 360;
        double rotatedCwAngle = (cwAngle + 90) % 360;
        return rotatedCwAngle;
    }

    public org.locationtech.spatial4j.shape.Point getStartingPoint() {
        return convertFromWebMercatorToWG84(currentSegment.getCoordinateN(0));
    }
  
    @Override
    public String toString() {
        return String.format( "%s -> %s",
        currentSegment.getCoordinateN(0).toString(), currentSegment.getCoordinateN(1).toString());
    }


}
