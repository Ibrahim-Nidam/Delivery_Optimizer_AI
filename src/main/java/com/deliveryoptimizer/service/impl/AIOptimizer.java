package com.deliveryoptimizer.service.impl;

import com.deliveryoptimizer.annotation.OptimizerType;
import com.deliveryoptimizer.config.OptimizerProperties;
import com.deliveryoptimizer.model.Delivery;
import com.deliveryoptimizer.model.DeliveryHistory;
import com.deliveryoptimizer.model.Tour;
import com.deliveryoptimizer.repository.DeliveryHistoryRepository;
import com.deliveryoptimizer.service.interfaces.TourOptimizer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@OptimizerType("AI")
@Transactional
@ConditionalOnProperty(name = "ai.enabled", havingValue = "true")
public class AIOptimizer implements TourOptimizer {

    private final OptimizerProperties properties;
    private final GeminiService geminiService;
    private final DeliveryHistoryRepository deliveryHistoryRepository;
    private final ObjectMapper objectMapper;

    public AIOptimizer(
            OptimizerProperties properties,
            DeliveryHistoryRepository deliveryHistoryRepository,
            GeminiService geminiService
    ) {
        this.properties = properties;
        this.deliveryHistoryRepository = deliveryHistoryRepository;
        this.geminiService = geminiService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<Delivery> optimizerTour(Tour tour) {
        String provider = properties.getProvider();

        if ("gemini".equalsIgnoreCase(provider)) {
            return optimizeWithGemini(tour);
        } else if ("local".equalsIgnoreCase(provider)) {
            throw new UnsupportedOperationException("Local Ollama provider not yet implemented");
        }

        throw new UnsupportedOperationException("Unknown AI provider: " + provider);
    }

    private List<Delivery> optimizeWithGemini(Tour tour) {
        List<DeliveryHistory> history = deliveryHistoryRepository.findAll();

        String systemInstruction = buildSystemInstruction();
        String jsonInput = buildJsonInput(tour, history);

        System.out.println("=== JSON Input to AI ===");
        System.out.println(jsonInput);

        String response = geminiService.generateContentWithSystemInstruction(systemInstruction, jsonInput);

        System.out.println("=== JSON Response from AI ===");
        System.out.println(response);

        List<Long> orderedIds = parseJsonResponse(response);
        return reorderDeliveries(tour.getDeliveries(), orderedIds);
    }

    private String buildSystemInstruction() {
        return "You are an intelligent logistics optimization assistant. " +
                "You receive JSON input with delivery data and historical patterns. " +
                "You must respond with ONLY a valid JSON object in this exact format: " +
                "{\"optimizedSequence\": [1, 2, 3]} where the array contains delivery IDs in optimal order. " +
                "Optimize by minimizing total distance while considering historical delay patterns. " +
                "Your response must be parseable JSON with no additional text, explanations, or markdown formatting.";
    }

    private String buildJsonInput(Tour tour, List<DeliveryHistory> history) {
        try {
            Map<String, Object> input = new LinkedHashMap<>();

            // Add tour information
            input.put("tourId", tour.getId());
            input.put("warehouseLocation", Map.of(
                    "latitude", tour.getWarehouse().getAltitude(),
                    "longitude", tour.getWarehouse().getLongitude()
            ));

            // Add deliveries
            List<Map<String, Object>> deliveries = new ArrayList<>();
            for (Delivery d : tour.getDeliveries()) {
                Map<String, Object> delivery = new LinkedHashMap<>();
                delivery.put("id", d.getId());
                delivery.put("customerName", d.getCustomer().getName());
                delivery.put("latitude", d.getAltitude());
                delivery.put("longitude", d.getLongitude());
                delivery.put("weight", d.getMaxWeight());
                delivery.put("volume", d.getMaxVolume());
                deliveries.add(delivery);
            }
            input.put("deliveries", deliveries);

            // Add historical data
            if (!history.isEmpty()) {
                List<Map<String, Object>> historicalData = new ArrayList<>();
                history.stream().limit(50).forEach(h -> {
                    Map<String, Object> hist = new LinkedHashMap<>();
                    hist.put("customerName", h.getCustomer().getName());
                    hist.put("delayMinutes", h.getDelayMinutes());
                    hist.put("dayOfWeek", h.getDayOfWeek());
                    historicalData.add(hist);
                });
                input.put("historicalData", historicalData);
            } else {
                input.put("historicalData", List.of());
            }

            // Add task description
            input.put("task", "Optimize the delivery sequence to minimize total distance traveled. " +
                    "Start from the warehouse, visit all deliveries, and return to warehouse. " +
                    "If historical data shows certain customers have delays on specific days, " +
                    "consider visiting them earlier. Return the delivery IDs in optimal order.");

            // Convert to JSON string
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(input);

        } catch (Exception e) {
            throw new RuntimeException("Failed to build JSON input: " + e.getMessage(), e);
        }
    }

    private List<Long> parseJsonResponse(String response) {
        try {
            // Remove any markdown code blocks if present
            String cleaned = response.replaceAll("```json\\n?|```\\n?", "").trim();

            // Parse JSON response
            JsonNode root = objectMapper.readTree(cleaned);

            // Try to get optimizedSequence array
            JsonNode sequenceNode = root.path("optimizedSequence");
            if (sequenceNode.isArray()) {
                List<Long> ids = new ArrayList<>();
                for (JsonNode node : sequenceNode) {
                    ids.add(node.asLong());
                }
                System.out.println("Parsed " + ids.size() + " delivery IDs from AI response");
                return ids;
            }

            // Fallback: if response is just an array of IDs
            if (root.isArray()) {
                List<Long> ids = new ArrayList<>();
                for (JsonNode node : root) {
                    ids.add(node.asLong());
                }
                System.out.println("Parsed " + ids.size() + " delivery IDs from array response");
                return ids;
            }

            System.err.println("Failed to parse AI JSON response - no optimizedSequence found");
            System.err.println("Response: " + response);
            return Collections.emptyList();

        } catch (Exception e) {
            System.err.println("Failed to parse AI response as JSON: " + response);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private List<Delivery> reorderDeliveries(List<Delivery> deliveries, List<Long> orderedIds) {
        if (orderedIds.isEmpty()) {
            System.out.println("No ordered IDs received, returning original order");
            return deliveries;
        }

        Map<Long, Delivery> deliveryMap = deliveries.stream()
                .collect(Collectors.toMap(Delivery::getId, d -> d));

        List<Delivery> ordered = new ArrayList<>();
        for (Long id : orderedIds) {
            if (deliveryMap.containsKey(id)) {
                ordered.add(deliveryMap.get(id));
            } else {
                System.err.println("Warning: AI suggested delivery ID " + id + " which doesn't exist in tour");
            }
        }

        // Add any missing deliveries at the end
        for (Delivery d : deliveries) {
            if (!ordered.contains(d)) {
                System.out.println("Adding missing delivery " + d.getId() + " to end of route");
                ordered.add(d);
            }
        }

        System.out.println("Reordered " + ordered.size() + " deliveries based on AI optimization");
        return ordered;
    }
}