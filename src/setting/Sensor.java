package setting;

import java.util.ArrayList;

public class Sensor {
    private final double LAMDA = Config.LAMDA;
    private final int ALPHA = Config.ALPHA;
    private Point center;
    private Point direct;
    private int dir;
    private double radius;

    public ArrayList<Point> listPoint = new ArrayList<Point>();

    public Sensor(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, double radius){
        listPoint.add(new Point(x1, y1));
        listPoint.add(new Point(x2, y2));
        listPoint.add(new Point(x3, y3));
        listPoint.add(new Point(x4, y4));
        this.radius = radius;
        center = new Point(x1, y1);
        direct = new Point(listPoint.get(1).getX(),listPoint.get(1).getY());
        dir = 1;
    }

    public Sensor(int W, int H, double R) {
        Point p1 = new Point((int) (Math.random() * W), (int) (Math.random() * H));
        Point p3 = new Point((int) (Math.random() * W), (int) (Math.random() * H));
        Point p2 = new Point(p1.getX(), p3.getY());
        Point p4 = new Point(p3.getX(), p1.getY());
        listPoint.add(p1);
        listPoint.add(p2);
        listPoint.add(p3);
        listPoint.add(p4);
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public static double d(Point p1, Point p2) {

        double i = Math.abs(p1.getX() - p2.getX() + p1.getY() - p2.getY());
        return i;
    }

    public void move(double s) {
        if (d(direct, center) > s) {
            if (direct.getX() == center.getX()) {
                if (direct.getY() > center.getY()) {
                    center.setY(center.getY() + s);
                }
                else {
                    center.setY(center.getY() - s);
                }
            } else {
                if (direct.getX() > center.getX()) {
                    center.setX(center.getX() + s);
                }
                else {
                    center.setX(center.getX() - s);
                }
            }
        }
        else {
            double tmp = s - d(direct, center);
            center.setX(direct.getX());
            center.setY(direct.getY());
            dir = (dir + 1) % 4;
            direct.setX(listPoint.get(dir).getX());
            direct.setY(listPoint.get(dir).getY());
            move(tmp);
        }
    }

    public void setDefault() {
        center.setX(listPoint.get(0).getX());
        center.setY(listPoint.get(0).getY());
        direct.setX(listPoint.get(1).getX());
        direct.setY(listPoint.get(1).getY());
        dir = 1;
    }

    public double MEx(double x, double y) {
        double distance = Math.sqrt((center.getX() - x) * (center.getX() - x) + (center.getY() - y) * (center.getY() - y));
        return LAMDA / Math.pow(distance, ALPHA);
    }
}
