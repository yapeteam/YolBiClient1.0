package dev.tenacity.ui.login;

public class Point {//定义圆心
    private double x;
    private double y;

    public Point(double x, double y) {//定义一个未知点坐标
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double distance(Point dot) {//点到圆心的距离公式
        return Math.sqrt(Math.pow(x - dot.x, 2)) + Math.pow(y - dot.y, 2);
    }
}
