package ru.javawebinar.topjava.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IdCounter {
    private static AtomicInteger counter = new AtomicInteger(0);

    private IdCounter(){}

    public static int incrementAndGetCounter() {
        return counter.incrementAndGet();
    }
}
