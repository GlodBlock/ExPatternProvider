package com.glodblock.github.extendedae.util;

import java.util.ArrayList;
import java.util.List;

public class LazyInits {

    private static final List<Runnable> INIT_F = new ArrayList<>();
    private static final List<Runnable> INIT_C = new ArrayList<>();

    public static void addFinal(Runnable task) {
        INIT_F.add(task);
    }

    public static void addCommon(Runnable task) {
        INIT_C.add(task);
    }

    public static void initCommon() {
        INIT_C.forEach(Runnable::run);
        INIT_C.clear();
    }

    public static void initFinal() {
        INIT_F.forEach(Runnable::run);
        INIT_F.clear();
    }

}
