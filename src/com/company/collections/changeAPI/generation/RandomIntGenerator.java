package com.company.collections.changeAPI.generation;

import java.util.Random;

public class RandomIntGenerator implements Generator<Integer> {

    // ====================================
    //               FIELDS
    // ====================================

    private final Random random;
    private final int minRange;
    private final int maxRange;

    // ====================================
    //             CONSTRUCTOR
    // ====================================

    public RandomIntGenerator(
            final int maxRange
    ) {
        this.maxRange = maxRange;
        this.minRange = 0;
        this.random = new Random();
    }

    public RandomIntGenerator(
            final int minRange,
            final int maxRange
    ) {
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.random = new Random();
    }

    public RandomIntGenerator(
            final int minRange,
            final int maxRange,
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
    public Integer generate() {
        return random.nextInt(minRange, maxRange);
    }

}
