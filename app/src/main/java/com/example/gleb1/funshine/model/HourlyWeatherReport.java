package com.example.gleb1.funshine.model;


public class HourlyWeatherReport {
    private int hourlyTemp;
    private int temp00;
    private int temp03;
    private int temp06;
    private int temp09;
    private int temp12;
    private int temp15;
    private int temp18;
    private int temp21;
    private String formattedHourlyListDate;

    /*public HourlyWeatherReport(int hourlyTemp, String formattedHourlyListDate) {
        this.hourlyTemp = hourlyTemp;
        this.formattedHourlyListDate = rawDateToPretty(formattedHourlyListDate);
    }*/

    public HourlyWeatherReport(int temp00, int temp03,int temp06, int temp09, int temp12, int temp15, int temp18, int temp21, String formattedHourlyListDate) {
        this.temp00 = temp00;
        this.temp03 = temp03;
        this.temp06 = temp06;
        this.temp09 = temp09;
        this.temp12 = temp12;
        this.temp15 = temp15;
        this.temp18 = temp18;
        this.temp21 = temp21;
        this.formattedHourlyListDate = rawDateToPretty(formattedHourlyListDate);
    }

    public String rawDateToPretty(String rawDate){
        int tempMonth = Integer.parseInt(rawDate.substring(5,7));
        String tempDay = rawDate.substring(8,10);
        String tempTime = rawDate.substring(11,16);
        return tempTime;
    }

    public int getHourlyTemp() {
        return hourlyTemp;
    }

    public String getFormattedHourlyListDate() {
        return formattedHourlyListDate;
    }

    public int getTemp00() {
        return temp00;
    }

    public int getTemp03() {
        return temp03;
    }

    public int getTemp06() {
        return temp06;
    }

    public int getTemp09() {
        return temp09;
    }

    public int getTemp12() {
        return temp12;
    }

    public int getTemp15() {
        return temp15;
    }

    public int getTemp18() {
        return temp18;
    }

    public int getTemp21() {
        return temp21;
    }
}
