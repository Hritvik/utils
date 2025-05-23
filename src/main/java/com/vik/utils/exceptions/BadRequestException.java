package com.vik.utils.exceptions;

import lombok.*;

@Getter
public class BadRequestException extends DownStreamException {
    private Integer statusCode;
    private final String message;

    public BadRequestException(Integer statusCode, String message) {
        super(statusCode, message);
        this.message = message;
        this.statusCode = statusCode;
    }
}
