package com.sivaji.weather.webservices.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InvalidZipCodeException extends RuntimeException {
    public InvalidZipCodeException(String exception) {
        super(exception);
    }
}
