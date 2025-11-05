package com.deliveryoptimizer.mapper;

import com.deliveryoptimizer.dto.WarehouseDTO;
import com.deliveryoptimizer.model.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    WarehouseDTO toDTO(Warehouse warehouse);

    @Mapping(target = "tours", ignore = true)
    Warehouse toEntity(WarehouseDTO dto);
}