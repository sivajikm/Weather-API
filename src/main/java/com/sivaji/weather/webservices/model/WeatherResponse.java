package com.sivaji.weather.webservices.model;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherResponse implements Serializable {

    private double windSpeed;
    private double windDirection;

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(double windDirection) {
        this.windDirection = windDirection;
    }

    @JsonProperty("wind")
    public void setWindDetails(Map<String, Object> oWind) {
        setWindSpeed( Double.parseDouble( oWind.get( "speed" ).toString() ) );
        if(oWind.get( "deg" ) != null) {
            setWindDirection( Double.parseDouble( oWind.get( "deg" ).toString() ) );
        }
    }

}
