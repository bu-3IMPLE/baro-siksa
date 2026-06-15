package com.team3imple.barosiksa.domain.tables.dto;

import com.team3imple.barosiksa.domain.tables.entity.RestaurantTable;
import com.team3imple.barosiksa.domain.tables.entity.TableStatus;

public record TableResponse(
        Long tableId,
        String tableNumber,
        Integer capacity,
        TableStatus status
) {
    public TableResponse(RestaurantTable table) {
        this(table.getId(), table.getTableNumber(), table.getCapacity(), table.getStatus());
    }
}
