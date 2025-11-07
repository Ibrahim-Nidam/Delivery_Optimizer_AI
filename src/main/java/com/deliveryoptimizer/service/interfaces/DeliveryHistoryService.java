package com.deliveryoptimizer.service.interfaces;

import com.deliveryoptimizer.dto.DeliveryHistoryDTO;
import com.deliveryoptimizer.dto.DeliveryHistoryReportDTO;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

public interface DeliveryHistoryService {
    DeliveryHistoryDTO createFromDelivery(DeliveryHistoryDTO dto);
    Page<DeliveryHistoryReportDTO> getDeliveryHistoryReport(Integer minDelay, Integer maxDelay, Pageable pageable);
}
