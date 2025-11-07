package com.deliveryoptimizer.service.impl;

import com.deliveryoptimizer.dto.DeliveryDTO;
import com.deliveryoptimizer.dto.DeliveryHistoryDTO;
import com.deliveryoptimizer.mapper.DeliveryMapper;
import com.deliveryoptimizer.model.Customer;
import com.deliveryoptimizer.model.Delivery;
import com.deliveryoptimizer.model.enums.DeliveryStatus;
import com.deliveryoptimizer.repository.CustomerRepository;
import com.deliveryoptimizer.repository.DeliveryRepository;
import com.deliveryoptimizer.service.interfaces.DeliveryHistoryService;
import com.deliveryoptimizer.service.interfaces.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final CustomerRepository customerRepository;
    private final DeliveryHistoryService deliveryHistoryService;


    @Override
    public DeliveryDTO createDelivery(DeliveryDTO dto){
        Delivery delivery = deliveryMapper.toEntity(dto);
        delivery.setStatus(DeliveryStatus.PENDING);

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer Not Found !"));
        delivery.setCustomer(customer);

        Delivery saved = deliveryRepository.save(delivery);
        return deliveryMapper.toDTO(saved);
    }

    @Override
    public List<DeliveryDTO> getAllDeliveries(){
        return deliveryRepository.findAll().stream()
                .map(deliveryMapper::toDTO)
                .toList();
    }

    @Override
    public DeliveryDTO getDeliveryById(Long id){
        return deliveryRepository.findById(id)
                .map(deliveryMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Delivery Not Found !"));
    }

    @Override
    public DeliveryDTO updateDelivery(Long id, DeliveryDTO dto){
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery Not Found !"));

        delivery.setAltitude(dto.getAltitude());
        delivery.setLongitude(dto.getLongitude());
        delivery.setMaxVolume(dto.getMaxVolume());
        delivery.setMaxWeight(dto.getMaxWeight());
        delivery.setTimeSlot(dto.getTimeSlot());

        delivery.setStatus(DeliveryStatus.PENDING);

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer Not Found !"));
        delivery.setCustomer(customer);

        Delivery saved = deliveryRepository.save(delivery);
        return deliveryMapper.toDTO(saved);
    }

    @Override
    public void deleteDelivery(Long id){
        deliveryRepository.deleteById(id);
    }

    @Override
    public DeliveryDTO markDelivered(Long deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + deliveryId));

        if (delivery.getStatus() == DeliveryStatus.DELIVERED) {
            throw new RuntimeException("Delivery already marked as delivered");
        }

        delivery.setStatus(DeliveryStatus.DELIVERED);
        delivery.setActualTime(LocalDateTime.now());
        Delivery saved = deliveryRepository.save(delivery);

        if (delivery.getCustomer() != null && delivery.getTour() != null) {

            LocalDateTime plannedTime = null;

            if (delivery.getTimeSlot() != null && delivery.getTimeSlot().contains("-")) {
                try {
                    String start = delivery.getTimeSlot().split("-")[0].trim();

                    plannedTime = LocalDateTime.now()
                            .withHour(Integer.parseInt(start.split(":")[0]))
                            .withMinute(Integer.parseInt(start.split(":")[1]))
                            .withSecond(0)
                            .withNano(0);

                } catch (Exception e) {
                    // Parsing failed, plannedTime stays null
                }
            }

            DeliveryHistoryDTO historyDTO = DeliveryHistoryDTO.builder()
                    .customerId(delivery.getCustomer().getId())
                    .tourId(delivery.getTour().getId())
                    .plannedTime(plannedTime)
                    .actualTime(delivery.getActualTime())
                    .build();

            deliveryHistoryService.createFromDelivery(historyDTO);
        }

        return deliveryMapper.toDTO(saved);
    }

}
