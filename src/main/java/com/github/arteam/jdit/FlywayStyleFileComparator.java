package com.github.arteam.jdit;

import java.util.Comparator;

/**
 * Compares file names in the Flyway style: (V1__, V2__,.., V10__, etc).
 */
public class FlywayStyleFileComparator implements Comparator<String> {

    private final Comparator<String> delegate = Comparator.comparingLong(value -> {
        if (!value.startsWith("V")) {
            return Integer.MAX_VALUE;
        }
        int separatorIndex = value.indexOf("__");
        if (separatorIndex == -1) {
            return Integer.MAX_VALUE;
        }
        return Long.parseLong(value.substring(1, separatorIndex));
    });

    @Override
    public int compare(String o1, String o2) {
        return delegate.compare(o1, o2);
    }
}
