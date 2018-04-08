package com.sivaji.weather.webservices.service;


import com.sivaji.weather.webservices.exception.InvalidZipCodeException;
import com.sivaji.weather.webservices.model.Weather;
import com.sivaji.weather.webservices.utils.WeatherServiceProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@PropertySource("classpath:application.properties")
public class WeatherServiceTest {

    private WeatherService weatherService;

    @Value("${app.weather.api.key}")
    private WeatherServiceProperties serviceProperties;

    @MockBean
    private RestTemplate mapperMock;

    @MockBean
    private ValidationUtils validationUtilsMock;

    private Weather weather;


    @Before
    public void setUp() {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder( );
        weatherService = new WeatherService(restTemplateBuilder, serviceProperties);
        weather = new Weather();
        weather.setWindSpeed( 5.1 );
        weather.setWindDirection( 330 );
    }

    @Test
    public void whenCityCodeIsProvided_thenRetrievedCityNameIsCorrect() {
        Weather weather = weatherService.getWindByZipCode("08810");
        assertThat(weather.getName()).isEqualTo("New Brunswick");
    }

    @Test(expected = InvalidZipCodeException.class)
    public void whenInvalidCityCodeIsProvided_thenInvalidCityCodeExceptionIsThrown() {
        weatherService.getWindByZipCode("088X");
    }
}