package dev.drone_tracking;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.operation.distance.DistanceOp;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import static dev.drone_tracking.Utils.*;

import java.util.ArrayList;
import java.util.List;

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
        return Utils.RadianToAzimuth(angleRadians);
    }



    public Double getBetterAzimuth(org.locationtech.spatial4j.shape.Point spatialPoint, double radius) {
        Point point = geometryFactory.createPoint(convertFromWG84ToWebMercator(spatialPoint));
        Geometry circle = point.buffer(radius);
        Geometry intersection = circle.intersection(currentSegment);
        Double result = Angle.angle(point.getCoordinate(), currentSegment.getCoordinateN(1));;
        if (intersection instanceof LineString) {
            LineString intersectionPoints = (LineString) intersection;
            for (int i = 0; i < intersectionPoints.getNumPoints(); i++) {
                Point intersectionPoint = intersectionPoints.getPointN(i);
                double angleToIntersectionPoint = Angle.angle(point.getCoordinate(), intersectionPoint.getCoordinate());
                if ( Math.abs(result-angleToIntersectionPoint) < Math.PI/2 ) {
                    result = angleToIntersectionPoint;
                }
            }
        } 
        return Utils.RadianToAzimuth(result);
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
