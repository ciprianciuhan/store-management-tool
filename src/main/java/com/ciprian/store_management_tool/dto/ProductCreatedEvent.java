package com.ciprian.store_management_tool.dto;

import java.math.BigDecimal;

public record ProductCreatedEvent(String barcode, String name, BigDecimal price) {

}
