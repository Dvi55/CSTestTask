package com.kislun.cstest.user;

import lombok.Getter;

import java.util.Map;

@Getter
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message, Map<String, String> requiredFields) {
        super(message + " " + requiredFields);
    }
}
