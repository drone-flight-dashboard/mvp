package dev.drone_tracking;

import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.ShapeFactory;

import java.util.*;


public class Main {
    static private final SpatialContext ctx = SpatialContext.GEO;
    static private final ShapeFactory shapeFactory = ctx.getShapeFactory();

    static private final int POINTS_QTY = 600;

    public static void main(String[] args) {
        Point point1 = shapeFactory.pointLatLon(0, 0); 

        Point point2 = shapeFactory.pointLatLon(0, 0.2); 
        Point point3 = shapeFactory.pointLatLon(0.2, 0.2); 

        //Route route = new Route(List.of(point1, point2, point3));
        FlightImitation flightImitation = new FlightImitation(List.of(point1, point2, point3));
        for(Point point: flightImitation) {

        }
    }


  
}