package com.sivaji.weather.webservices.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

@ConfigurationProperties(prefix = "app.weather")
@Configuration("serviceProperties")
public class WeatherServiceProperties {

    @Valid
    private final Api api = new Api();

    public Api getApi() {
        return this.api;
    }


    public static class Api {

        @NotNull
        private String key;

        public String getKey() {
            return this.key;
        }

        public void setKey(String key) {
            this.key = key;
        }

    }

}
