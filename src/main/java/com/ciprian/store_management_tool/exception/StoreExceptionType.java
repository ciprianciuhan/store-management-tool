package com.ciprian.store_management_tool.exception;

import lombok.Getter;

@Getter
public enum StoreExceptionType {
    AUTHENTICATION_REQUIRED("Authentication is required to access this resource"),
    TOKEN_EXPIRED("Authentication token has expired"),
    TOKEN_INVALID("Authentication token is invalid"),
    ACCESS_DENIED("Access denied to the requested resource, you do not have the correct role");

    private final String message;

    StoreExceptionType(String message) {
        this.message = message;
    }

}
