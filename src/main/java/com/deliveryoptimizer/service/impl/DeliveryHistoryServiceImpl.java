package com.deliveryoptimizer.service.impl;

import com.deliveryoptimizer.dto.DeliveryHistoryDTO;
import com.deliveryoptimizer.dto.DeliveryHistoryReportDTO;
import com.deliveryoptimizer.mapper.DeliveryHistoryMapper;
import com.deliveryoptimizer.model.Customer;
import com.deliveryoptimizer.model.Delivery;
import com.deliveryoptimizer.model.DeliveryHistory;
import com.deliveryoptimizer.model.Tour;
import com.deliveryoptimizer.repository.CustomerRepository;
import com.deliveryoptimizer.repository.DeliveryHistoryRepository;
import com.deliveryoptimizer.repository.DeliveryRepository;
import com.deliveryoptimizer.repository.TourRepository;
import com.deliveryoptimizer.service.interfaces.DeliveryHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryHistoryServiceImpl implements DeliveryHistoryService {

    private final DeliveryHistoryRepository deliveryHistoryRepository;
    private final DeliveryRepository deliveryRepository;
    private final CustomerRepository customerRepository;
    private final TourRepository tourRepository;
    private final DeliveryHistoryMapper deliveryHistoryMapper;

    @Override
    public DeliveryHistoryDTO createFromDelivery(DeliveryHistoryDTO dto) {
        // Fetch linked entities
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Tour tour = tourRepository.findById(dto.getTourId())
                .orElseThrow(() -> new RuntimeException("Tour not found"));

        // Build entity
        DeliveryHistory history = DeliveryHistory.builder()
                .customer(customer)
                .tour(tour)
                .deliveryDate(LocalDate.now())
                .plannedTime(dto.getPlannedTime())
                .actualTime(dto.getActualTime() != null ? dto.getActualTime() : LocalDateTime.now())
                .delayMinutes(calculateDelay(dto.getPlannedTime(), dto.getActualTime()))
                .dayOfWeek(LocalDate.now().getDayOfWeek().toString())
                .build();

        DeliveryHistory saved = deliveryHistoryRepository.save(history);
        return deliveryHistoryMapper.toDTO(saved);
    }

    // Helper to calculate delay in minutes between planned and actual time
    private Integer calculateDelay(LocalDateTime planned, LocalDateTime actual) {
        if (planned == null || actual == null) return 0;
        long minutes = ChronoUnit.MINUTES.between(planned, actual);
        return (int) Math.max(minutes, 0);
    }

    @Override
    public Page<DeliveryHistoryReportDTO> getDeliveryHistoryReport(Integer minDelay, Integer maxDelay, Pageable pageable){
        return deliveryHistoryRepository.findByDelayBetween(minDelay, maxDelay, pageable);
    }

}
