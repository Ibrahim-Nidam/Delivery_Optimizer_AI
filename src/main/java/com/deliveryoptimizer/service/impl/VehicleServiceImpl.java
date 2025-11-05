package com.deliveryoptimizer.service.impl;

import com.deliveryoptimizer.dto.VehicleDTO;
import com.deliveryoptimizer.mapper.VehicleMapper;
import com.deliveryoptimizer.model.Vehicle;
import com.deliveryoptimizer.repository.VehicleRepository;
import com.deliveryoptimizer.service.interfaces.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;


    @Override
    public VehicleDTO createVehicle(VehicleDTO dto){
        Vehicle vehicle = vehicleMapper.toEntity(dto);

        vehicle.setMaxWeight(vehicle.getType().getMaxWeightKg());
        vehicle.setMaxVolume(vehicle.getType().getMaxVolumeM3());
        vehicle.setMaxDeliveries(vehicle.getType().getMaxDeliveries());

        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toDTO(saved);
    }

    @Override
    public List<VehicleDTO> getAllVehicles(){
        return vehicleRepository.findAll().stream()
                .map(vehicleMapper::toDTO)
                .toList();

    }

    @Override
    public VehicleDTO getVehicleById(Long id){
        return vehicleRepository.findById(id)
                .map(vehicleMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Vehicle Not Found !"));
    }

    @Override
    public VehicleDTO updateVehicle(Long id, VehicleDTO dto){
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle Not Found !"));

        vehicle.setName(dto.getName());
        vehicle.setType(dto.getType());

        vehicle.setMaxWeight(vehicle.getType().getMaxWeightKg());
        vehicle.setMaxVolume(vehicle.getType().getMaxVolumeM3());
        vehicle.setMaxDeliveries(vehicle.getType().getMaxDeliveries());

        Vehicle saved = vehicleRepository.save(vehicle);

        return vehicleMapper.toDTO(saved);
    }

    @Override
    public void deleteVehicle(Long id){
        vehicleRepository.deleteById(id);
    }
}
