package com.deliveryoptimizer.service.impl;

import com.deliveryoptimizer.dto.DeliveryDTO;
import com.deliveryoptimizer.mapper.DeliveryMapper;
import com.deliveryoptimizer.model.Delivery;
import com.deliveryoptimizer.repository.DeliveryRepository;
import com.deliveryoptimizer.service.interfaces.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;


    @Override
    public DeliveryDTO createDelivery(DeliveryDTO dto){
        Delivery delivery = deliveryMapper.toEntity(dto);
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
        delivery.setStatus(dto.getStatus());
        delivery.setTimeSlot(dto.getTimeSlot());

        Delivery saved = deliveryRepository.save(delivery);

        return deliveryMapper.toDTO(saved);
    }

    @Override
    public void deleteDelivery(Long id){
        deliveryRepository.deleteById(id);
    }
}
