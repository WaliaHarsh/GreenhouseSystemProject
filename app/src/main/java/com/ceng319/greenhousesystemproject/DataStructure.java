package com.ceng319.greenhousesystemproject;

public class DataStructure {
    private String name;
    private String temperature;
    private String humidity;
    private String message;
    private String timestamp;

    public DataStructure(){

    }
    public DataStructure(String humidity, String message,String name, String temperature,  String timestamp) {
        this.name = name;
        this.temperature = temperature;
        this.humidity = humidity;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}