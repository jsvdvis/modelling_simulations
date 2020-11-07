package nl.rug.modellingsimulations.utilities;

import java.awt.geom.AffineTransform;
import java.util.Collection;

public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAngle(Point other) {
        return Math.atan2(other.getY() - getY(), other.getX() - getX());
    }

    public double getDistance(Point other) {
        double dy = other.getY() - getY();
        double dx = other.getX() - getX();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public Point getHalfWay(Point other) {
        return new Point((this.x + other.x) / 2, (this.y + other.y) / 2);
    }

    public static Point avgPoint(Collection<Point> points) {
        double xAvg = points.stream().mapToDouble(Point::getX).average().orElseThrow();
        double yAvg = points.stream().mapToDouble(Point::getY).average().orElseThrow();
        return new Point(xAvg, yAvg);
    }

    public Point rotate_around(Point centerPoint, double angleDegrees) {
//        return new Point(
//                Math.cos(angleDegrees) * (this.getX() - centerPoint.getX()) - Math.sin(angleDegrees) * (this.y - centerPoint.y) + centerPoint.x,
//                Math.sin(angleDegrees) * (this.x - centerPoint.getX()) + Math.cos(angleDegrees) * (this.y - centerPoint.y) + centerPoint.y
//        );
        // The code above gives a slight error. Below is more reliable.
        double[] pt = {x, y};
        AffineTransform.getRotateInstance(Math.toRadians(angleDegrees), centerPoint.x, centerPoint.y)
                .transform(pt, 0, pt, 0, 1); // specifying to use this double[] to hold coords
        return new Point(pt[0], pt[1]);
    }

    public Point moveTowards(Point other, double distance) {
        Point unitVector = getUnitVector(other);
        return new Point(getX() + unitVector.getX() * distance, getY() + unitVector.getY() * distance);
    }

    public Point getVector(Point other) {
        return new Point(other.getX() - getX(), other.getY() - getY());
    }

    public Point getUnitVector(Point other) {
        Point vector = getVector(other);
        double size = Math.sqrt(vector.getX() * vector.getX() + vector.getY() * vector.getY());
        return new Point(vector.getX() / size, vector.getY() / size);
    }
}
