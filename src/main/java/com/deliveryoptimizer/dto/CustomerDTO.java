package com.deliveryoptimizer.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDTO {
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String preferredTimeSlot;
}
