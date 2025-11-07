package com.deliveryoptimizer.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryHistoryReportDTO {
    private Long deliveryId;
    private Long customerId;
    private String customerName;
    private Long tourId;
    private LocalDate deliveryDate;
    private LocalDateTime plannedTime;
    private LocalDateTime actualTime;
    private Integer delayMinutes;
    private String dayOfWeek;
}
