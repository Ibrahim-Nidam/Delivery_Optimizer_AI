package com.deliveryoptimizer.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Column(name = "planned_time")
    private LocalDateTime plannedTime;

    @Column(name = "actual_time")
    private LocalDateTime actualTime;

    @Column(name = "delay_minutes")
    private Integer delayMinutes;

    @Column(name = "day_of_week")
    private String dayOfWeek;
}
