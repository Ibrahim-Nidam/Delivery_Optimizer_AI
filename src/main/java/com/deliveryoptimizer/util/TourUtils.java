package com.deliveryoptimizer.util;

import com.deliveryoptimizer.model.Delivery;
import com.deliveryoptimizer.model.Warehouse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class TourUtils {
    private TourUtils(){}

    public static double calculateTotalDistance(Warehouse warehouse, List<Delivery> deliveries, DistanceCalculator distanceCalculator){
        if(warehouse == null || deliveries == null || deliveries.isEmpty()) return 0;

        double totalDistance = 0;
        double currentLat = warehouse.getAltitude();
        double currentLon = warehouse.getLongitude();

        for (Delivery d : deliveries){
            totalDistance += distanceCalculator.distance(currentLat, currentLon, d.getAltitude(), d.getLongitude());
            currentLat = d.getAltitude();
            currentLon = d.getLongitude();
        }

        totalDistance += distanceCalculator.distance(currentLat, currentLon, warehouse.getAltitude(), warehouse.getLongitude());

        return totalDistance;
    }

    public static String formatDistance(double distanceInKm){
        if(distanceInKm < 1){
            return String.format("%.0f m", distanceInKm * 1000);
        } else {
            return String.format("%.1f Km", distanceInKm);
        }
    }

    public static LocalDateTime parseTimeSlotToDateTime(LocalDate date, String timeSlot) {
        if (timeSlot == null || timeSlot.isEmpty()) {
            return date.atStartOfDay();
        }

        // Split the slot "HH:mm-HH:mm"
        String[] parts = timeSlot.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid time slot format: " + timeSlot);
        }

        String startTimeStr = parts[0].trim();
        LocalTime startTime = LocalTime.parse(startTimeStr);

        return LocalDateTime.of(date, startTime);
    }
}
