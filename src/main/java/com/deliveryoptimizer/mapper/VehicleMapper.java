package com.deliveryoptimizer.mapper;

import com.deliveryoptimizer.dto.VehicleDTO;
import com.deliveryoptimizer.model.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    VehicleDTO toDTO(Vehicle vehicle);

    @Mapping(target = "tours", ignore = true)
    Vehicle toEntity(VehicleDTO dto);
}