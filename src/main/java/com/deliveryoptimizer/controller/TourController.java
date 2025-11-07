package com.deliveryoptimizer.controller;

import com.deliveryoptimizer.dto.TourDTO;
import com.deliveryoptimizer.service.interfaces.TourService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tours")
public class TourController {
    private final TourService tourService;

    public TourController(TourService tourService){
        this.tourService = tourService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TourDTO createTour(@Valid @RequestBody TourDTO dto){
        return tourService.createTour(dto);
    }

    @GetMapping
    public List<TourDTO> getAllTours(){
        return tourService.getAllTours();
    }

    @GetMapping("/{id}")
    public TourDTO getTourById(@PathVariable Long id){
        return tourService.getTourById(id);
    }

    @PutMapping("/{id}")
    public TourDTO updateTour(@PathVariable Long id, @Valid @RequestBody TourDTO dto){
        return tourService.updateTour(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTour(@PathVariable Long id){
        tourService.deleteTour(id);
    }

    @PostMapping("/{id}/deliveries")
    @ResponseStatus(HttpStatus.OK)
    public TourDTO addDeliveriesToTour(@PathVariable Long id, @Valid @RequestBody List<Long> deliveryIds){
        return tourService.addDeliveriesToTour(id, deliveryIds);
    }

    /**
     * Single endpoint to optimize tour using the configured optimizer from application.yml
     * The optimizer type is controlled by ai.optimizer.type property (NN, CW, or AI)
     */
    @GetMapping("/{id}/optimize")
    public List<Long> optimizeTour(@PathVariable Long id){
        return tourService.optimizeTour(id);
    }

    /**
     * Get distances for all available optimization methods for comparison
     */
    @GetMapping("/{id}/distances")
    public Map<String, String> getTotalDistances(@PathVariable Long id){
        return tourService.getTourDistances(id);
    }
}