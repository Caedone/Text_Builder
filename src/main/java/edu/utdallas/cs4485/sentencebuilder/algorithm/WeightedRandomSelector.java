package edu.utdallas.cs4485.sentencebuilder.algorithm;

import java.util.*;

/**
 * Implements weighted random selection for Markov chain generation.
 * Selects items based on their probability weights.
 *
 * @author CS4485 Team
 * @version 1.0
 */
public class WeightedRandomSelector<T> {

    private Random random;

    /**
     * Constructor.
     */
    public WeightedRandomSelector() {
        this.random = new Random();
    }

    /**
     * Constructor with seed for reproducible results.
     *
     * @param seed random seed
     */
    public WeightedRandomSelector(long seed) {
        this.random = new Random(seed);
    }

    /**
     * Selects a random item based on weights.
     *
     * @param items map of items to their weights
     * @return selected item, or null if items is empty
     */
    public T select(Map<T, Double> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        // Calculate total weight
        double totalWeight = items.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        if (totalWeight <= 0) {
            // If all weights are 0, select uniformly
            List<T> itemList = new ArrayList<>(items.keySet());
            return itemList.get(random.nextInt(itemList.size()));
        }

        // Generate random value between 0 and totalWeight
        double randomValue = random.nextDouble() * totalWeight;

        // Find the item corresponding to this value
        double cumulativeWeight = 0.0;
        for (Map.Entry<T, Double> entry : items.entrySet()) {
            cumulativeWeight += entry.getValue();
            if (randomValue <= cumulativeWeight) {
                return entry.getKey();
            }
        }

        // Fallback (shouldn't reach here due to floating point)
        return items.keySet().iterator().next();
    }

    /**
     * Selects a random item from a list (uniform distribution).
     *
     * @param items list of items
     * @return selected item, or null if list is empty
     */
    public T selectUniform(List<T> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        return items.get(random.nextInt(items.size()));
    }

    /**
     * Selects multiple random items based on weights without replacement.
     *
     * @param items map of items to their weights
     * @param count number of items to select
     * @return list of selected items
     */
    public List<T> selectMultiple(Map<T, Double> items, int count) {
        if (items == null || items.isEmpty() || count <= 0) {
            return Collections.emptyList();
        }

        List<T> selected = new ArrayList<>();
        Map<T, Double> remaining = new HashMap<>(items);

        for (int i = 0; i < count && !remaining.isEmpty(); i++) {
            T item = select(remaining);
            if (item != null) {
                selected.add(item);
                remaining.remove(item);
            }
        }

        return selected;
    }

    /**
     * Converts a frequency map to a probability map.
     *
     * @param frequencies map of items to their frequencies
     * @return map of items to their probabilities
     */
    public static <T> Map<T, Double> frequenciesToProbabilities(Map<T, Integer> frequencies) {
        if (frequencies == null || frequencies.isEmpty()) {
            return Collections.emptyMap();
        }

        int total = frequencies.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        if (total == 0) {
            return Collections.emptyMap();
        }

        Map<T, Double> probabilities = new HashMap<>();
        for (Map.Entry<T, Integer> entry : frequencies.entrySet()) {
            double probability = (double) entry.getValue() / total;
            probabilities.put(entry.getKey(), probability);
        }

        return probabilities;
    }
}