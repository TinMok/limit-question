package org.example;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SimpleStatisticsUtil {

    private SimpleStatisticsUtil() {}

    private static final Queue<Long> queue = new ConcurrentLinkedQueue<>();

    public static boolean addQueue(Long value) {
        return queue.add(value);
    }

    public static Long getMax() {
        return queue.stream().max(Long::compare).orElse(0L);
    }

    public static Long getMin() {
        return queue.stream().min(Long::compare).orElse(100000L);
    }

    public static Long getAverage() {
        return queue.stream().reduce(0L, Long::sum) / queue.size();
    }

    public static int getQueueCount() {
        return queue.size();
    }

}
