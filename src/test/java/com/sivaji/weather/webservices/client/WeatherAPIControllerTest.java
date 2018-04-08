package com.sivaji.weather.webservices.client;

import com.sivaji.weather.webservices.model.Weather;
import com.sivaji.weather.webservices.service.WeatherService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@WebMvcTest(WeatherAPIController.class)
public class WeatherAPIControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    WeatherService weatherServiceMock;

    private String zipCode = "08831";
    private Weather weather;

    @Before
    public void setup() {
        weather = new Weather();
        weather.setName("New Brunswick"  );
        weather.setWindSpeed( 5.1 );
        weather.setWindDirection( 330 );
    }

    @Test
    public void whenCityCodeIsProvided_thenResponseStatusIs200() throws Exception {
        given(weatherServiceMock.getWindByZipCode(zipCode)).willReturn(weather);
        mockMvc.perform(get("/api/v1/wind/"+zipCode))
                .andExpect(status().isOk());
    }

}
