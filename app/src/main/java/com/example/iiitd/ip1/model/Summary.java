package com.example.iiitd.ip1.model;

/**
 * Created by iiitd on 27/6/17.
 */

public class Summary {
    private String tag;
    private int averageY;
    private int minimumY;
    private int maximumY;
    private int currentY;

    private String averageX;
    private String minimumX;
    private String maximumX;
    private String currentX;

    public Summary(String tag, int averageY, int minimumY, int maximumY, int currentY, String averageX, String minimumX, String maximumX, String currentX) {
        this.tag = tag;
        this.averageY = averageY;
        this.minimumY = minimumY;
        this.maximumY = maximumY;
        this.currentY = currentY;
        this.averageX = averageX;
        this.minimumX = minimumX;
        this.maximumX = maximumX;
        this.currentX = currentX;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getAverageY() {
        return averageY;
    }

    public void setAverageY(int averageY) {
        this.averageY = averageY;
    }

    public int getMinimumY() {
        return minimumY;
    }

    public void setMinimumY(int minimumY) {
        this.minimumY = minimumY;
    }

    public int getMaximumY() {
        return maximumY;
    }

    public void setMaximumY(int maximumY) {
        this.maximumY = maximumY;
    }

    public int getCurrentY() {
        return currentY;
    }

    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }

    public String getAverageX() {
        return averageX;
    }

    public void setAverageX(String averageX) {
        this.averageX = averageX;
    }

    public String getMinimumX() {
        return minimumX;
    }

    public void setMinimumX(String minimumX) {
        this.minimumX = minimumX;
    }

    public String getMaximumX() {
        return maximumX;
    }

    public void setMaximumX(String maximumX) {
        this.maximumX = maximumX;
    }

    public String getCurrentX() {
        return currentX;
    }

    public void setCurrentX(String currentX) {
        this.currentX = currentX;
    }



}
