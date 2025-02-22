package dev.drone_tracking;

import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.ShapeFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;
import dev.drone_tracking.dto.DroneTrackPositionData;
import  dev.drone_tracking.DynamoInteraction.DynamoClient;


public class Main {
    static private final SpatialContext ctx = SpatialContext.GEO;
    static private final ShapeFactory shapeFactory = ctx.getShapeFactory();
    private static final String HOST = "localhost";
    private static final int PORT = 5010;
    static private final int POINTS_QTY = 600;
    static DatagramSocket socket;
    static private final DynamoClient dynamoWriter = new DynamoClient();
   

    
    public static void main(String[] args) {
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        Point point1 = shapeFactory.pointLatLon(0, 0); 
        Point point2 = shapeFactory.pointLatLon(0, 0.05); 
        Point point3 = shapeFactory.pointLatLon(1, 0.05); 

        FlightImitation flightImitation = new FlightImitation(List.of(point1, point2, point3), "xxx", 1000l);
        flightImitation.getDTOStream().limit(POINTS_QTY).forEach(Main::sendPoint);

    }

    private static void sendPoint(DroneTrackPositionData dtoToSend) {
        dynamoWriter.putData(dtoToSend);
        byte[] buffer = dtoToSend.toString().getBytes();
        try {
			DatagramPacket packet =
					new DatagramPacket(buffer, buffer.length,
							InetAddress.getByName(HOST), PORT);
			socket.send(packet);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }


  
}