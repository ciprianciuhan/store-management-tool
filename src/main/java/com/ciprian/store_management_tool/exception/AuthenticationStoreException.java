package com.ciprian.store_management_tool.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthenticationStoreException extends StoreException {
    private final HttpStatus httpStatus;
    private final StoreExceptionType exceptionType;

    public AuthenticationStoreException(StoreExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
        this.httpStatus = HttpStatus.UNAUTHORIZED;
    }

    public AuthenticationStoreException(StoreExceptionType exceptionType, String additionalDetails) {
        super(exceptionType.getMessage() + ": " + additionalDetails);
        this.exceptionType = exceptionType;
        this.httpStatus = HttpStatus.UNAUTHORIZED;
    }

}
