package nl.rug.modellingsimulations.utilities;

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
}
