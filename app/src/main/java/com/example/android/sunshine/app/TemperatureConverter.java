package com.example.android.sunshine.app;


public class TemperatureConverter {

    public static String metricToImperial(String fullString) {
        String[] splitString = fullString.split("-");
        String temperatures = splitString[2].trim();
        String[] maxAndMin = temperatures.split("/");
        String max = metricToImperialString(Integer.valueOf(maxAndMin[0]));
        String min = metricToImperialString(Integer.valueOf(maxAndMin[1]));

        return splitString[0] + "-" + splitString[1] + "- " + max + "/" + min;
    }

    public static int metricToImperialInteger(int celcius) {
        return (int) Math.round((double)celcius * 1.8 + 32);
    }

    public static String metricToImperialString(int celcius) {
        return String.valueOf(metricToImperialInteger(celcius));
    }

}
