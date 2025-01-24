package dev.drone_tracking;

import org.locationtech.spatial4j.shape.Point;

import static org.locationtech.spatial4j.distance.DistanceUtils.KM_TO_DEG;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class FlightImitation implements Iterable<Point> {
    static private final double NEXT_POINT_DELTA_METERS = 50;
    static private final double NEXT_POINT_DELTA_DEG = NEXT_POINT_DELTA_METERS / 1000 * KM_TO_DEG;
    Route route;
    String droneName;
    long txInterval;

    public FlightImitation(List<Point> routePoints, String droneName, Long txInterval) {
        this.droneName = droneName;
        this.txInterval = txInterval;
        route = new Route(routePoints);
    }

    @Override
    public Iterator<Point> iterator() {
        return new Iterator<Point>() {
            Iterator<RouteSegment> routeSegmentIterator = route.iterator();
            RouteSegment currentRouteSegment = routeSegmentIterator.next();
            Point next = currentRouteSegment.getStartingPoint();
            boolean needToAdvanceOnNextSegment = false;

            @Override
            public boolean hasNext() {
                return (next == null && !routeSegmentIterator.hasNext()) ? false : true;
            }

            @Override
            public Point next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                Point result = next;
                if (needToAdvanceOnNextSegment) {
                    needToAdvanceOnNextSegment = false;
                    if (routeSegmentIterator.hasNext()) {
                        currentRouteSegment = routeSegmentIterator.next();
                        next = getNextPoint();
                    } else {
                        next = null;
                    }
                }
                PointToRouteMetrics pointToRouteMetrics = null;
                if (next != null) {
                    next = getNextPoint();
                    pointToRouteMetrics = currentRouteSegment.getNearestPointAndDistance(result);
                    if (pointToRouteMetrics.nearestPointIsSegmentEndPoint()) {
                        needToAdvanceOnNextSegment = true;
                    }
                }
                // FIXIT
                // Debugging purpose
                if (result != null && pointToRouteMetrics != null) {
                    printPointDeviationInfo(result, pointToRouteMetrics);
                }
                return result;

            }

            private void printPointDeviationInfo(Point point, PointToRouteMetrics pointToRouteMetrics) {
                StringBuilder str = new StringBuilder();
                str.append("Curr. point ");
                str.append(Utils.formatSpacialPoint(point, 5));
                str.append(" Nearest route point ");
                str.append(Utils.formatSpacialPoint(pointToRouteMetrics.nearestPoint(), 5));
                str.append( String.format(" Dist. %.1f", pointToRouteMetrics.minDistance()));
//                str.append(String.format(" Azimuth: %.1f", currentRouteSegment.getAzimithToTheEndPointOfCurrentSegment(point)));
                System.out.println(str.toString());
            }

            private Point getNextPoint() {
                double driftAngle = Utils.getDriftAngle(0.7,10);
                return Utils.distanceCalculator.pointOnBearing(next, NEXT_POINT_DELTA_DEG,
                        currentRouteSegment.getBetterAzimuth(next, 200) + driftAngle, Utils.ctx,
                        null);
            }

        };
    }

}
