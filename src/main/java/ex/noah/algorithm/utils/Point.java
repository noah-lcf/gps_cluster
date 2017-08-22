package ex.noah.algorithm.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by noah on 2016/5/30.
 */


public class Point {

    private float x = 0;
    private float y = 0;
    private Object objId;
    private int orderKey;

    private int cluster_number = 0;

    public Point(float x, float y) {
        this.setX(x);
        this.setY(y);
    }

    public Point(float x, float y,Object objId) {
        this.setX(x);
        this.setY(y);
        this.objId=objId;
    }

    public Integer getOrderKey() {
        return orderKey;
    }

    public void setOrderKey(int orderKey) {
        this.orderKey = orderKey;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getX() {
        return this.x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getY() {
        return this.y;
    }

    public void setCluster(int n) {
        this.cluster_number = n;
    }

    public int getCluster() {
        return this.cluster_number;
    }

    //Calculates the distance between two points.
    protected static double distance(Point p, Point centroid) {
        return DistanceUtils.getDistance(p.getX(),p.getY(),centroid.getX(),centroid.getY());
//        return Math.sqrt(Math.pow((centroid.getY() - p.getY()), 2) + Math.pow((centroid.getX() - p.getX()), 2));
    }

    //Creates random point
    protected static Point createRandomPoint(int min, int max) {
        Random r = new Random();
        float x = (float) (min + (max - min) * r.nextDouble());
        float y = (float) (min + (max - min) * r.nextDouble());
        return new Point(x, y);
    }

    protected static List createRandomPoints(int min, int max, int number) {
        List points = new ArrayList(number);
        for (int i = 0; i < number; i++) {
            points.add(createRandomPoint(min, max));
        }
        return points;
    }

    public String toString() {
        return "objId:"+objId+",point(" + x + "," + y + ")";
    }
}
