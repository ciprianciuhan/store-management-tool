package com.ciprian.store_management_tool.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DuplicateProductException extends StoreException {
    private final HttpStatus httpStatus;
    private final StoreExceptionType exceptionType;

    public DuplicateProductException(String barcode) {
        super(String.format("Product with barcode %s already exists", barcode));
        this.httpStatus = HttpStatus.CONFLICT;
        this.exceptionType = StoreExceptionType.DUPLICATE_PRODUCT;
    }
}
