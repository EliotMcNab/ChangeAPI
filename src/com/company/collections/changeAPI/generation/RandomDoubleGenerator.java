package com.company.collections.changeAPI.generation;

import java.util.Random;

public class RandomDoubleGenerator implements Generator<Double> {

    // ====================================
    //               FIELDS
    // ====================================

    private final Random random;
    private final double minRange;
    private final double maxRange;

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public RandomDoubleGenerator(
            final double maxRange
    ) {
        this.maxRange = maxRange;
        this.minRange = 0;
        this.random = new Random();
    }

    public RandomDoubleGenerator(
            final double minRange,
            final double maxRange
    ) {
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.random = new Random();
    }

    public RandomDoubleGenerator(
            final double minRange,
            final double maxRange,
            final long seed
    ) {
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.random = new Random(seed);
    }

    // ====================================
    //             GENERATION
    // ====================================

    @Override
    public Double generate() {
        return random.nextDouble(minRange, maxRange);
    }
}
