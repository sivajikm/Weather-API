package com.sivaji.weather.webservices.client;

import com.sivaji.weather.webservices.exception.InvalidZipCodeException;
import com.sivaji.weather.webservices.exception.WeatherServiceException;
import com.sivaji.weather.webservices.model.CustomResponse;
import com.sivaji.weather.webservices.model.Weather;
import com.sivaji.weather.webservices.service.WeatherService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@Validated
@RestController
@RequestMapping("/api/v1")
public class WeatherAPIController {

    private final WeatherService weatherService;

    public WeatherAPIController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @RequestMapping(value = "/wind/{zipCode}", method = RequestMethod.GET)
    public Weather getWindInformation(@PathVariable String zipCode)
    {
        if(this.weatherService.getWind(zipCode) == null) {
            throw new WeatherServiceException("No Response");
        }

        return this.weatherService.getWind(zipCode);
    }

    @RequestMapping(value = "/wind/clearcache", method = RequestMethod.GET)
    public ResponseEntity<?> clearCache() {
        this.weatherService.clearCache();
        return new ResponseEntity(new CustomResponse("Cache Cleared"),
                HttpStatus.OK);
    }


}
