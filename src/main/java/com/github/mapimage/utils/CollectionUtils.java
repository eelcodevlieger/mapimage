package com.github.mapimage.utils;

import java.util.Collections;
import java.util.Random;

/**
 * Taken from {@link Collections#shuffle(java.util.List)} source.
 * See http://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
 */
public class CollectionUtils {

    /**
     * Code from method java.util.Collections.shuffle() - updated to use a fixed-seed pseudo-random instance for repeatability
     * note: Array is shuffled in place - return value for convenience.
     */
    public static void shuffle(final float[][] array) {
        // use new (pseudo) random instance with fixed seed for each shuffle, to reproduce the same output between runs
        var random = new Random(1);
        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i));
        }
    }

    private static void swap(final float[][] array, final int i, final int j) {
        var temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}