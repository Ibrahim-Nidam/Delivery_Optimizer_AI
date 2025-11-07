package com.deliveryoptimizer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 1, max = 255)
    private String name;

    @Size(max = 500)
    private String address;

    private double latitude;

    private double longitude;

    @Column(name = "preferred_time_slot")
    private String preferredTimeSlot;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<DeliveryHistory> deliveryHistories;
}
