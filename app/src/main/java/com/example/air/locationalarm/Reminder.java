package com.example.air.locationalarm;

public class Reminder {
    public int ID;
    public String title;
    public String detail;

    public Reminder(){
    ID = 0;
    title = "";
    detail = "";
    }
    Reminder(String title, String detail){

        this.title = title;
        this.detail = detail;
    }
    Reminder(int id, String title, String detail){
        this.ID = id;
        this.title = title;
        this.detail = detail;
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
