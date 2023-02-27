package com.example.eventstracetobpmnchorconverter.util;

import java.util.Random;

public class RandomIDGenerator {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final int LENGTH = 8;

    public static String generate() {
        final var random = new Random();
        final var builder = new StringBuilder();

        for (int i = 0; i < LENGTH; i++) {
            final var index = random.nextInt(ALPHABET.length());
            builder.append(ALPHABET.charAt(index));
        }

        return builder.toString();
    }

    public static String generateWithPrefix(final String prefix) {
        final var random = new Random();
        final var builder = new StringBuilder();
        builder.append(prefix);
        builder.append("_");

        for (int i = 0; i < LENGTH; i++) {
            final var index = random.nextInt(ALPHABET.length());
            builder.append(ALPHABET.charAt(index));
        }

        return builder.toString();
    }

}
