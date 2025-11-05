package com.deliveryoptimizer.mapper;

import com.deliveryoptimizer.dto.TourDTO;
import com.deliveryoptimizer.model.Delivery;
import com.deliveryoptimizer.model.Tour;
import com.deliveryoptimizer.model.Vehicle;
import com.deliveryoptimizer.model.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TourMapper {

    @Mapping(source = "vehicle", target = "vehicleId", qualifiedByName = "vehicleToVehicleId")
    @Mapping(source = "warehouse", target = "warehouseId", qualifiedByName = "warehouseToWarehouseId")
    @Mapping(source = "deliveries", target = "deliveryIds", qualifiedByName = "deliveriesToDeliveryIds")
    TourDTO toDTO(Tour tour);

    @Mapping(source = "vehicleId", target = "vehicle", qualifiedByName = "vehicleIdToVehicle")
    @Mapping(source = "warehouseId", target = "warehouse", qualifiedByName = "warehouseIdToWarehouse")
    @Mapping(target = "deliveries", ignore = true)
    Tour toEntity(TourDTO dto);

    @Named("vehicleToVehicleId")
    default Long vehicleToVehicleId(Vehicle vehicle) {
        return vehicle != null ? vehicle.getId() : null;
    }

    @Named("warehouseToWarehouseId")
    default Long warehouseToWarehouseId(Warehouse warehouse) {
        return warehouse != null ? warehouse.getId() : null;
    }

    @Named("deliveriesToDeliveryIds")
    default List<Long> deliveriesToDeliveryIds(List<Delivery> deliveries) {
        if (deliveries == null) {
            return null;
        }
        return deliveries.stream()
                .map(Delivery::getId)
                .collect(Collectors.toList());
    }

    @Named("vehicleIdToVehicle")
    default Vehicle vehicleIdToVehicle(Long vehicleId) {
        if (vehicleId == null) {
            return null;
        }
        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        return vehicle;
    }

    @Named("warehouseIdToWarehouse")
    default Warehouse warehouseIdToWarehouse(Long warehouseId) {
        if (warehouseId == null) {
            return null;
        }
        Warehouse warehouse = new Warehouse();
        warehouse.setId(warehouseId);
        return warehouse;
    }
}