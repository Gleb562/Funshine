package com.example.gleb1.funshine.model;


public class TodayWeatherReport {

    public static final String WEATHER_TYPE_CLOUDS = "Clouds";
    public static final String WEATHER_TYPE_CLEAR = "Clear";
    public static final String WEATHER_TYPE_RAIN = "Rain";
    public static final String WEATHER_TYPE_WIND = "Wind";
    public static final String WEATHER_TYPE_SNOW = "Snow";
    public static final String WEATHER_TYPE_THUNDERSTORM = "Thunderstorm";

    private int currentTemperature;
    private int todayTempMin;
    private String todayWeatherType;

    public TodayWeatherReport(int currentTemperature, int todayTempMin,String todayWeatherType) {
        this.currentTemperature = currentTemperature;
        this.todayTempMin = todayTempMin;
        this.todayWeatherType = todayWeatherType;
    }

    public int getCurrentTemperature() {
        return currentTemperature;
    }

    public int getTodayTempMin() {
        return todayTempMin;
    }

    public String getTodayWeatherType() {
        return todayWeatherType;
    }
}
