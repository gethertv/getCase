package me.gethertv.getcase.data;

import org.bukkit.Location;

public class MoveHead {
    double height = 0.25;
    double maxHeight;
    double minHeight;

    boolean up;

    public MoveHead(Location location)
    {
        maxHeight = location.getY() + height;
        minHeight = location.getY() - height;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(double maxHeight) {
        this.maxHeight = maxHeight;
    }

    public double getMinHeight() {
        return minHeight;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public void setMinHeight(double minHeight) {
        this.minHeight = minHeight;
    }

}
