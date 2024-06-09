package com.knocksfornometer.mapimage.utils;

import java.util.Collections;
import java.util.Random;

/**
 * Taken from {@link Collections#shuffle(java.util.List)} source.
 * See http://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
 */
public class CollectionUtils {
    private static Random random;

    /**
     * Code from method java.util.Collections.shuffle();
     * note: Array is shuffled in place - return value for convenience.
     */
    public static void shuffle(float[][] array) {
        if (random == null) random = new Random();
        int count = array.length;
        for (int i = count; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i));
        }
    }

    private static void swap(float[][] array, int i, int j) {
        float[] temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}