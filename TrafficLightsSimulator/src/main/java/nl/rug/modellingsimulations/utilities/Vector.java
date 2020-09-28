package nl.rug.modellingsimulations.utilities;

public class Vector {
    private double dx;
    private double dy;

    public Vector(Point start, Point end) {
        dx = end.getX() - start.getX();
        dy = end.getY() - start.getY();

        // Normalize vector.
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length > 0) {
            dx /= length;
            dy /= length;
        }
    }

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    public double getRelativeAngle(Vector other) {
        double angle = Math.acos(dx * other.getDx() + dy * other.getDy()) * 180.0 / Math.PI;

        if ((other.getDx() * -dy + other.getDy() * dx) > 0) {
            return 360 - angle;
        }
        return angle;
    }
}
