package com.github.arteam.jdit.util;

import java.util.Comparator;

public class MigrationFileComparator implements Comparator<String> {

    private final Comparator<String> comparator = Comparator.comparingInt(value -> {
        if (!value.startsWith("V")) {
            return Integer.MAX_VALUE;
        }
        int separatorIndex = value.indexOf("__");
        return Integer.parseInt(value.substring(1, separatorIndex));
    });

    @Override
    public int compare(String o1, String o2) {
        return comparator.compare(o1, o2);
    }
}
