package com.deliveryoptimizer.repository;

import com.deliveryoptimizer.dto.DeliveryHistoryReportDTO;
import com.deliveryoptimizer.model.DeliveryHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface DeliveryHistoryRepository extends JpaRepository<DeliveryHistory, Long> {
    List<DeliveryHistory> findByCustomerId(Long customerId);
    List<DeliveryHistory> findByTourId(Long tourId);

    @Query("""
           SELECT new com.deliveryoptimizer.dto.DeliveryHistoryReportDTO(
               d.id,
               d.customer.id,
               d.customer.name,
               d.tour.id,
               d.deliveryDate,
               d.plannedTime,
               d.actualTime,
               d.delayMinutes,
               d.dayOfWeek
           )
           FROM DeliveryHistory d
           WHERE (:minDelay IS NULL OR d.delayMinutes >= :minDelay)
             AND (:maxDelay IS NULL OR d.delayMinutes <= :maxDelay)
           ORDER BY d.delayMinutes DESC
           """)
    Page<DeliveryHistoryReportDTO> findByDelayBetween(
            @Param("minDelay") Integer minDelay,
            @Param("maxDelay") Integer maxDelay,
            Pageable pageable
    );
}
