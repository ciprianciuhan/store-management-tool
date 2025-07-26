package com.ciprian.store_management_tool.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AccessDeniedStoreException extends StoreException {
    private final HttpStatus httpStatus;
    private final StoreExceptionType exceptionType;

    public AccessDeniedStoreException(StoreExceptionType exceptionType, String additionalDetails) {
        super(exceptionType.getMessage() + ": " + additionalDetails);
        this.exceptionType = exceptionType;
        this.httpStatus = HttpStatus.FORBIDDEN;
    }

}
