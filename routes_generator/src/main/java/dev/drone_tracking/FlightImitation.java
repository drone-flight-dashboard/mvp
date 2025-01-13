// package dev.drone_tracking;

// import org.locationtech.spatial4j.shape.Point;

// import java.util.Iterator;
// import java.util.List;
// import java.util.NoSuchElementException;

// public class FlightImitation implements Iterable<Point> {
//     Route route;
//     Point nextPoint;
//     public FlightImitation(List<Point> routePoints) {
//         route = new Route(routePoints);
//         nextPoint = routePoints.getFirst();
//     }

//     @Override
//     public Iterator<Point> iterator() {
//         return new Iterator<Point>() {
            
//             @Override
//             public boolean hasNext() {
//                 return nextPoint == null ? false : true;
//             }

//             @Override
//             public Point next() {
//                 if(!hasNext()) {
//                     throw new NoSuchElementException();
//                 }
//                 Point result = nextPoint;

                
//             }
            
//         }
//     } 

// }
