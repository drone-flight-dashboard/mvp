package dev.drone_tracking;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.locationtech.jts.geom.LineString;

import org.locationtech.jts.geom.Coordinate;
import static dev.drone_tracking.Utils.*;

public class Route implements Iterable<RouteSegment> {

    static final double EPSILON = 1e-3;

    LineString route;

    public Route(List<org.locationtech.spatial4j.shape.Point> pointsList) {
        // FIXIT
        // route consist of two distinct points minimum
        // create convertor for a list of coordinate, not for single one for efficiancy
        // of use
        Coordinate[] points = pointsList.stream().map(Utils::convertFromWG84ToWebMercator).toArray(Coordinate[]::new);
        route = geometryFactory.createLineString(points);
    }

    @Override
    public Iterator<RouteSegment> iterator() {
        return new Iterator<RouteSegment>() {
            int currentEndPointIndex = 1;
            RouteSegment next = getSegmentBasedOnIndex(currentEndPointIndex - 1);

            @Override
            public boolean hasNext() {
                return next == null ? false : true;
            }

            @Override
            public RouteSegment next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                RouteSegment result = next;
                next = getSegmentBasedOnIndex(currentEndPointIndex - 1);
                currentEndPointIndex++;
                return result;

            }

            private RouteSegment getSegmentBasedOnIndex(int index) {
                RouteSegment result = null;
                if (route.getNumPoints()  > currentEndPointIndex) {
                    Coordinate start = route.getCoordinateN(index);
                    Coordinate end = route.getCoordinateN(index + 1);
                    result = new RouteSegment(geometryFactory.createLineString(new Coordinate[] { start, end }));
                    currentEndPointIndex++;
                }
                return result;
            }

        };
    }

}
