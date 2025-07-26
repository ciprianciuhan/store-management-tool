package com.ciprian.store_management_tool.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class StoreException extends RuntimeException {

    private final LocalDateTime timestamp;

    protected StoreException(String message) {
        super(message);
        this.timestamp = LocalDateTime.now();
    }

}
