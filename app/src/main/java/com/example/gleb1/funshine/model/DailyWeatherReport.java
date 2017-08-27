package com.example.gleb1.funshine.model;

/**
 * Created by Gleb1 on 22.08.2017.
 */

public class DailyWeatherReport {

    public static final String WEATHER_TYPE_CLOUDS = "Clouds";
    public static final String WEATHER_TYPE_CLEAR = "Clear";
    public static final String WEATHER_TYPE_RAIN = "Rain";
    public static final String WEATHER_TYPE_WIND = "Wind";
    public static final String WEATHER_TYPE_SNOW = "Snow";
    public static final String WEATHER_TYPE_THUNDERSTORM = "Thunderstorm";

    private String cityName;
    private String country;
    private int currentTemp;
    private int maxTemp;
    private int minTemp;
    private String weather;
    private String formatted_date;
    private String formatted_list_date;


    public DailyWeatherReport(String cityName, String country, int currentTemp, int maxTemp, int minTemp, String weather, String rawDate) {
        this.cityName = cityName;
        this.country = country;
        this.currentTemp = currentTemp;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.weather = weather;
        this.formatted_date = rawDateToPretty(rawDate);
        this.formatted_list_date = rawDateToPrettyList(rawDate);

    }



    public String rawDateToPretty(String rawDate){
        String [] months = {"January","February","March","April","May","June","July","August","September","October",
                "November","December"};
        int tempMonth = Integer.parseInt(rawDate.substring(5,7));
        String tempDay = rawDate.substring(8,10);
        String prettyDate =  "Today, " + months[tempMonth-1] + " " + tempDay;

        return prettyDate;
    }

    public String rawDateToPrettyList(String rawDate){
        String [] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sept","Oct",
                "Nov","Dec"};
        int tempMonth = Integer.parseInt(rawDate.substring(5,7));
        String tempDay = rawDate.substring(8,10);
        String prettyDate = months[tempMonth-1] + " " + tempDay;

        return prettyDate;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCountry() {
        return country;
    }

    public int getCurrentTemp() {
        return currentTemp;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public String getWeather() {
        return weather;
    }

    public String getFormatted_date() {
        return formatted_date;
    }

    public String getFormatted_list_date() {
        return formatted_list_date;
    }
}
