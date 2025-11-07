package com.deliveryoptimizer.mapper;

import com.deliveryoptimizer.dto.DeliveryHistoryDTO;
import com.deliveryoptimizer.model.DeliveryHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DeliveryHistoryMapper {
    DeliveryHistoryMapper INSTANCE = Mappers.getMapper(DeliveryHistoryMapper.class);

    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "tour.id", target = "tourId")
    DeliveryHistoryDTO toDTO(DeliveryHistory deliveryHistory);

    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "tourId", target = "tour.id")
    DeliveryHistory toEntity(DeliveryHistoryDTO dto);
}
