package com.lovoctech.yakshanaada.model;

public class Event {

    public static final int PLAYING = 0;
    public static final int PAUSED = 1;
    public static final int STOPPED = 2;
    public static final int APP_STARTED = 69;

    private Shruthi shruthi;
    private int status;

    public Event(Shruthi shruthi, int status) {
        this.shruthi = shruthi;
        this.status = status;
    }

    public Shruthi getShruthi() {
        return shruthi;
    }

    public void setShruthi(Shruthi shruthi) {
        this.shruthi = shruthi;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
