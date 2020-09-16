package nl.rug.modellingsimulations.utilities;

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
}
