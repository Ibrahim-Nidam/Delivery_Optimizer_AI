package com.deliveryoptimizer.controller;

import com.deliveryoptimizer.dto.DeliveryHistoryReportDTO;
import com.deliveryoptimizer.service.interfaces.DeliveryHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/deliveryHistory")
public class DeliveryHistoryController {
    private final DeliveryHistoryService deliveryHistoryService;

    public DeliveryHistoryController(DeliveryHistoryService deliveryHistoryService){
        this.deliveryHistoryService = deliveryHistoryService;
    }

    @GetMapping("/report")
    public Page<DeliveryHistoryReportDTO> getDeliveryHistoryReport(
            @RequestParam(required = false) Integer minDelay,
            @RequestParam(required = false) Integer maxDelay,
            Pageable pageable) {
        return deliveryHistoryService.getDeliveryHistoryReport(minDelay, maxDelay, pageable);
    }

}
