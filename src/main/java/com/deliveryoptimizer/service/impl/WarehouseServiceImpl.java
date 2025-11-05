package com.deliveryoptimizer.service.impl;

import com.deliveryoptimizer.dto.WarehouseDTO;
import com.deliveryoptimizer.mapper.WarehouseMapper;
import com.deliveryoptimizer.model.Warehouse;
import com.deliveryoptimizer.repository.WarehouseRepository;
import com.deliveryoptimizer.service.interfaces.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    @Override
    public WarehouseDTO createWarehouse(WarehouseDTO dto){
        Warehouse warehouse = warehouseMapper.toEntity(dto);
        Warehouse saved = warehouseRepository.save(warehouse);
        return warehouseMapper.toDTO(saved);
    }

    @Override
    public List<WarehouseDTO> getAllWarehouses(){
        return warehouseRepository.findAll().stream()
                .map(warehouseMapper::toDTO)
                .toList();
    }

    @Override
    public WarehouseDTO getWarehouseById(Long id){
        return warehouseRepository.findById(id)
                .map(warehouseMapper::toDTO)
                .orElseThrow(()-> new RuntimeException("Warehouse Not Found !"));
    }

    @Override
    public WarehouseDTO updateWarehouse(Long id, WarehouseDTO dto){
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Warehouse Not Found!"));

        warehouse.setAddress(dto.getAddress());
        warehouse.setAltitude(dto.getAltitude());
        warehouse.setLongitude(dto.getLongitude());
        warehouse.setOpenTime(dto.getOpenTime());
        warehouse.setCloseTime(dto.getCloseTime());

        Warehouse saved = warehouseRepository.save(warehouse);

        return warehouseMapper.toDTO(saved);
    }

    @Override
    public void deleteWarehouse(Long id){
        warehouseRepository.deleteById(id);
    }
}
