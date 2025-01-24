package dev.drone_tracking;

import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.ShapeFactory;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class Main {
    static private final SpatialContext ctx = SpatialContext.GEO;
    static private final ShapeFactory shapeFactory = ctx.getShapeFactory();
    private static final String HOST = "3.25.180.179";
    private static final int PORT = 5000;
    static private final int POINTS_QTY = 600;
    
    public static void main(String[] args) {

        Point point1 = shapeFactory.pointLatLon(0, 0); 
        Point point2 = shapeFactory.pointLatLon(0, 0.05); 
        Point point3 = shapeFactory.pointLatLon(1, 0.05); 

        FlightImitation flightImitation = new FlightImitation(List.of(point1, point2, point3), "xxx", 1000l);
        Stream<Point> streamOfPoints = StreamSupport.stream(flightImitation.spliterator(), false);

        streamOfPoints
            .peek(point -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            })
            .forEach(Main::sendPoint);
    }

    private static void sendPoint(Point point) {

    }


  
}