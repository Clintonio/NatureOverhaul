package com.natureoverhaul.util;

import java.util.Random;

public class XORShiftRandom extends Random {
    private long seed = System.nanoTime();

    protected int next(int nbits) {
        // N.B. Not thread-safe!
        long x = this.seed;
        x ^= (x << 21);
        x ^= (x >>> 35);
        x ^= (x << 4);
        this.seed = x;
        x &= ((1L << nbits) -1);
        return (int) x;
    }
}