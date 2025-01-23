package dev.drone_tracking;

import static org.locationtech.spatial4j.distance.DistanceUtils.KM_TO_DEG;

import java.util.Random;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.ShapeFactory;
import org.locationtech.spatial4j.distance.DistanceCalculator;
import org.locationtech.spatial4j.distance.GeodesicSphereDistCalc.Haversine;

public class Utils {
    static final double EPSILON = 1e-3;
    static final CRSFactory crsFactory = new CRSFactory();
    static final CoordinateTransformFactory transformFactory = new CoordinateTransformFactory();
    static final SpatialContext ctx = SpatialContext.GEO;
    static private final ShapeFactory shapeFactory = ctx.getShapeFactory();
    static final GeometryFactory geometryFactory = new GeometryFactory();
    static final DistanceCalculator distanceCalculator = new Haversine();
    static private Random random = new Random();

    static Coordinate convertFromWG84ToWebMercator(org.locationtech.spatial4j.shape.Point coordinate) {
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromName("EPSG:4326"); // WGS84
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromName("EPSG:3857"); // Web Mercator
        final CoordinateTransform transform = transformFactory.createTransform(sourceCRS, targetCRS);
        final ProjCoordinate fromCoordinate = new ProjCoordinate(coordinate.getLon(), coordinate.getLat());
        final ProjCoordinate toCoordinate = new ProjCoordinate();
        transform.transform(fromCoordinate, toCoordinate);
        return new Coordinate(toCoordinate.x, toCoordinate.y);
    }

    static org.locationtech.spatial4j.shape.Point convertFromWebMercatorToWG84(Coordinate coordinate) {
        final CoordinateReferenceSystem targetCRS = crsFactory.createFromName("EPSG:4326"); // WGS84
        final CoordinateReferenceSystem sourceCRS = crsFactory.createFromName("EPSG:3857"); // Web Mercator
        final CoordinateTransform transform = transformFactory.createTransform(sourceCRS, targetCRS);
        final ProjCoordinate fromCoordinate = new ProjCoordinate(coordinate.x, coordinate.y);
        final ProjCoordinate toCoordinate = new ProjCoordinate();
        transform.transform(fromCoordinate, toCoordinate);
        return shapeFactory.pointLatLon(toCoordinate.y, toCoordinate.x);
    }

    static boolean isTwoPointsEqial(Coordinate point1, Coordinate point2)  {
        return Math.abs(point1.x - point2.x) < EPSILON && Math.abs(point1.y - point2.y) < EPSILON;
    }

    static double getDriftAngle(double sigma, double maxAngle)  {
        random = new Random();
        double data = random.nextGaussian () * sigma;
        return (data + 3 * sigma) / (6 * sigma) * 2 * maxAngle - maxAngle;
    }
      static String formatSpacialPoint(Point point, int precision ) {
        String floatSpec = "%." + String.valueOf(precision) + "f";
        String format = "Lat. " + floatSpec + " Long. " + floatSpec;
        return String.format(format, point.getLat(), point.getLon());
    }

    static double RadianToAzimuth( double radian) {
        double angleDegrees = Math.toDegrees(radian);
        double cwAngle = (360 - angleDegrees) % 360;
        double rotatedCwAngle = (cwAngle + 90) % 360;
        return rotatedCwAngle;
    }

}
