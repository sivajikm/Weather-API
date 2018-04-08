package com.sivaji.weather.webservices.service;

import java.net.URI;
import java.util.regex.Pattern;

import com.sivaji.weather.webservices.exception.InvalidZipCodeException;
import com.sivaji.weather.webservices.exception.WeatherServiceException;
import com.sivaji.weather.webservices.model.Weather;
import com.sivaji.weather.webservices.utils.Cache;
import com.sivaji.weather.webservices.utils.CacheManager;
import com.sivaji.weather.webservices.utils.WeatherServiceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;



@Service
public class WeatherService {

    private static final String WEATHER_URL =
            "http://api.openweathermap.org/data/2.5/weather?zip={zipCode},us&APPID={key}";

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final CacheManager cacheManager = CacheManager.getInstance();
    private final String regex = "^\\d{5}(-\\d{4})?$";

    public WeatherService(RestTemplateBuilder restTemplateBuilder, WeatherServiceProperties serviceProperties) {
        this.restTemplate = restTemplateBuilder.build();
        this.apiKey = serviceProperties.getApi().getKey();
    }

    public Weather getWind(String zipCode) {
        logger.info("Requesting current wind for {}", zipCode);

        if(zipCode == null) {
            throw new InvalidZipCodeException("Zip Code can not be null");
        }

        if(!Pattern.matches(regex, zipCode)) {
            throw new InvalidZipCodeException("Invalid Zip Code");
        }

        Cache weatherAPICache = cacheManager.getCache(CacheManager.CACHE_NAME);

        if (weatherAPICache.isKeyInCache(zipCode)) {
            logger.info("Getting data from the Cache {}", weatherAPICache.isKeyInCache(zipCode));
            return (Weather) weatherAPICache.get(zipCode);
        }

        URI url = new UriTemplate(WEATHER_URL).expand(zipCode, this.apiKey);
        return invoke(url, zipCode, Weather.class);
    }


    private <T> T invoke(URI url, String zipCode, Class<T> responseType) {
        RequestEntity<?> request = RequestEntity.get(url)
                .accept(MediaType.APPLICATION_JSON).build();
        ResponseEntity<T> exchange = this.restTemplate
                .exchange(request, responseType);

        Cache weatherAPICache = cacheManager.getCache(CacheManager.CACHE_NAME);
        weatherAPICache.add( zipCode, exchange.getBody() );

        return exchange.getBody();
    }

    public void clearCache() {
        cacheManager.getCache(CacheManager.CACHE_NAME).removeAll();
    }

}