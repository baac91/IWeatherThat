package com.example.iweatherthat.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.String;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class DailyWeatherReport {
    public static final String WEATHER_TYPE_CLOUDS = "Clouds";
    public static final String WEATHER_TYPE_CLEAR = "Clear";
    public static final String WEATHER_TYPE_RAIN = "Rain";
    public static final String WEATHER_TYPE_WIND = "Wind";
    public static final String WEATHER_TYPE_SNOW = "Snow";


    private String cityName;
    private String country;
    private int currentTemp;
    private int maxTemp;
    private int minTemp;
    private String weather;
    private String formattedDate;

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

    public String getFormattedDate() {
        return formattedDate;
    }



    public DailyWeatherReport(String cityName, int currentTemp, int maxTemp, int minTemp,  String country, String weather, int rawDate) {
        this.cityName = cityName;
        this.country = country;
        this.currentTemp = currentTemp;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.weather = weather;
        this.formattedDate = rawDateToPretty(rawDate);
    }

    public String rawDateToPretty(int rawDate){
//        LocalDate localDate = LocalDate.ofEp
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL");
//        String formattedString = localDate.format(formatter);
        long seconds = rawDate;
        long millis = seconds * 1000;

        Date date = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedDate = sdf.format(date);
        System.out.println(formattedDate);


        return formattedDate;
//        return "Jan 1";
    }
}
