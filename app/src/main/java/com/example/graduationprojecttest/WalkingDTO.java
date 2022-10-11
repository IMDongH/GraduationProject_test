package com.example.graduationprojecttest;

public class WalkingDTO {
    double longitude; //위도
    double latitude; //경도
    double altitude; //고도
    String time; //측정 시각
    String timer; //타이머 시간
    int step; // 걸음 수

    @Override
    public String toString() {
        return "WalkingDTO{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", altitude=" + altitude +
                ", time='" + time + '\'' +
                ", timer='" + timer + '\'' +
                ", step=" + step +
                '}';
    }

    public WalkingDTO(double longitude, double latitude, double altitude, String time, String timer, int step) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.time = time;
        this.timer = timer;
        this.step = step;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
