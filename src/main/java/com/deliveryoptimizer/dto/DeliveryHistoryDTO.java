package com.deliveryoptimizer.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryHistoryDTO {
    private Long id;
    private Long customerId;
    private Long tourId;
    private LocalDate deliveryDate;
    private LocalDateTime plannedTime;
    private LocalDateTime actualTime;
    private Integer delayMinutes;
    private String dayOfWeek;
}
