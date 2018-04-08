package com.sivaji.weather.webservices.model;

public class CustomResponse extends Weather {

    private String message;

    public CustomResponse(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
