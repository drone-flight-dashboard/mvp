package dev.drone_tracking;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.ShapeFactory;
import org.locationtech.spatial4j.distance.DistanceCalculator;
import static org.locationtech.spatial4j.distance.DistanceUtils.KM_TO_DEG;

import java.util.*;

import org.locationtech.spatial4j.distance.GeodesicSphereDistCalc.Haversine;

public class Main {
    static private final SpatialContext ctx = SpatialContext.GEO;
    static private final ShapeFactory shapeFactory = ctx.getShapeFactory();
    static private final DistanceCalculator distanceCalculator = new Haversine();

    static private final double NEXT_POINT_DELTA_METERS = 50;
    static private final int POINTS_QTY = 500;
    static private final double NEXT_POINT_DELTA_DEG = NEXT_POINT_DELTA_METERS / 1000 * KM_TO_DEG;
    //static private final Point startingPoint = shapeFactory.pointLatLon(35, -115.2437);
    static private final int MAX_DEVIATION_METER = 100;
    static private double MAX_DRIFT_ANGLE = 2;
    static private final double MC = 1;
    static private final int MR = 20;

    public static void main(String[] args) {

        //Point point1 = shapeFactory.pointLatLon(34.0522, -118.2437); // Los Angeles
       // Point point2 = shapeFactory.pointLatLon(37.7749, -122.4194); // San Francisco
        Point point1 = shapeFactory.pointLatLon(0, 0); // Los Angeles

        Point point2 = shapeFactory.pointLatLon(0, 10); // San Francisco

        Route route = new Route(List.of(point1, point2));
        

        List<Point> points = new ArrayList<>();
        points.add(point1);
        Point nextPoint = point1;
        for (int i = 0; i < POINTS_QTY; i++) {
            double driftAngle = getDriftAngle();
            nextPoint = distanceCalculator.pointOnBearing(nextPoint, NEXT_POINT_DELTA_DEG, route.getAzimithToTheEndPointOfCurrentSegment(nextPoint) + driftAngle, ctx, null);
            points.add(nextPoint);
        }
        points.forEach(p -> printPointDeviationInfo(p,route));

        double latitude = 40.7128;
        double longitude = -74.0060;
        // Point point = shapeFactory.pointLatLon(latitude, longitude);

        // Point point1 = shapeFactory.pointLatLon(-118.2437, 34.0522); // Los Angeles
        // Point point2 = shapeFactory.pointLatLon(-122.4194, 37.7749); // San Francisco
        // var line = shapeFactory.lineString().pointLatLon(latitude, longitude);
        

    }

    static void printPointDeviationInfo(Point point, Route route) {
        StringBuilder str = new StringBuilder();
        str.append("Curr. point ");
        str.append( formatSpacialPoint(point, 5));
        str.append(" Nearest route point ");
        PointToRouteMetrics pointToRouteMetrics = route.getNearestPointAndDistance(point);
        str.append(formatSpacialPoint(pointToRouteMetrics.nearestPoint(), 5));
        str.append(" Dist. " + String.valueOf(pointToRouteMetrics.minDistance()));
        System.out.println(str.toString());
    }

    static String formatSpacialPoint(Point point, int precision ) {
        String floatSpec = "%." + String.valueOf(precision) + "f";
        String format = "Lat. " + floatSpec + " Long. " + floatSpec;
        return String.format(format, point.getLat(), point.getLon());
    }

    static double getDriftAngle()  {
        Random random = new Random();
        return random.nextGaussian () * 1.5;
    }
}