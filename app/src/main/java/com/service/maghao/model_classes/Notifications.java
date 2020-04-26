package com.service.maghao.model_classes;

public class Notifications {

    private int icon;
    private String notifDesc;
    private String date;

    public Notifications(int icon, String notifDesc, String date) {
        this.icon = icon;
        this.notifDesc = notifDesc;
        this.date = date;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getNotifDesc() {
        return notifDesc;
    }

    public void setNotifDesc(String notifDesc) {
        this.notifDesc = notifDesc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
