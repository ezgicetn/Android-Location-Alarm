package com.example.air.locationalarm;

public class Reminder {
    public int ID;
    public String title;
    public String detail;
    public double Lat, Lng;

    public Reminder(){
    ID = 0;
    Lat = 0.0;
    Lng = 0.0;
    title = "";
    detail = "";
    }
    Reminder(String title, String detail, double lat, double lng){
        this.title = title;
        this.detail = detail;
        this.Lat = lat;
        this.Lng = lng;
    }
    Reminder(int id, String title, String detail, double lat, double lng){
        this.ID = id;
        this.title = title;
        this.detail = detail;
        this.Lat = lat;
        this.Lng = lng;
    }
    public int getID(){
        return ID;
    }
    public String getTitle(){
        return title;
    }
    public String getDetail(){
        return detail;
    }
    public double getLat() {
        return Lat;
    }

    public double getLng() {
        return Lng;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public void setLng(double lng) {
        Lng = lng;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public void setDetail(String detail) {
        this.detail = detail;
    }
}
