package com.unipi.panapost.a13033sms;

public class model {
    String id,message,date;
    double x;
    double y;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public model(String id, String message) {
        this.id = id;
        this.message = message;
    }

    public model(String id, String message, String date, double x, double y) {
        this.id = id;
        this.message = message;
        this.date = date;
        this.x = x;
        this.y = y;
    }
}
