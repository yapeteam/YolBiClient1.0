package dev.tenacity.ui.login;

public class Circle {
    private double radius;//圆的半径
    private Point center;//圆心

    public Circle(double r) {
        radius = r;
    }

    public double area() {//计算面积的方法
        return Math.PI * radius * radius;
    }

    public Circle(double radius, Point center) {
        this.radius = radius;
        this.center = center;
    }

    public double girth() {//计算周长的方法
        return 2 * Math.PI * radius;
    }

    public double getRadius() {
        return radius;
    }

    public double setRadius(double radius) {
        return this.radius = radius;
    }

    public Point getCenter() {
        return center;
    }

    public Point setCenter(Point center) {
        return this.center = center;
    }

    public boolean isCircleIn(Point position) {//判断点是否在圆内
        return center.distance(position) <= radius;
    }
}

