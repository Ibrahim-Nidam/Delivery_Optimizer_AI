package com.deliveryoptimizer.mapper;

import com.deliveryoptimizer.dto.DeliveryDTO;
import com.deliveryoptimizer.model.Delivery;
import com.deliveryoptimizer.model.Tour;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    @Mapping(source = "tour", target = "tourId", qualifiedByName = "tourToTourId")
    @Mapping(target = "customerId", source = "customer.id")
    DeliveryDTO toDTO(Delivery delivery);

    @Mapping(source = "tourId", target = "tour", qualifiedByName = "tourIdToTour")
    @Mapping(target = "customer", ignore = true)
    Delivery toEntity(DeliveryDTO dto);

    @Named("tourToTourId")
    default Long tourToTourId(Tour tour) {
        return tour != null ? tour.getId() : null;
    }

    @Named("tourIdToTour")
    default Tour tourIdToTour(Long tourId) {
        if (tourId == null) {
            return null;
        }
        Tour tour = new Tour();
        tour.setId(tourId);
        return tour;
    }
}