package com.example.eventstracetobpmnchorconverter.tests.furchterman_reingold;

public class Coordinates {
    private float x;
    private float y;

    public Coordinates(float xx, float yy) {
        x = xx;
        y = yy;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String toString() {
        return x + " " + y;
    }

    public Coordinates subtract(Coordinates c) {
        return new Coordinates(x - c.x, y - c.y);
    }

    public Coordinates add(Coordinates c) {
        return new Coordinates(x + c.x, y + c.y);
    }

    public Coordinates unit() {
        if (length() == 0)
            return new Coordinates(0.000001f, 0.0000001f);
        else
            return new Coordinates(x / (float) Math.sqrt(x * x + y * y), y / (float) Math.sqrt(y * y + x * x));
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public float distance(Coordinates c) {
        return (float) Math.sqrt((x - c.x) * (x - c.x) + (y - c.y) * (y - c.y));
    }

    public Coordinates scale(float k) {
        return new Coordinates(k * x, k * y);
    }
}
