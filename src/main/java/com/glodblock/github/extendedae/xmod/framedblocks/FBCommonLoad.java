package com.glodblock.github.extendedae.xmod.framedblocks;

import com.glodblock.github.extendedae.container.pattern.ContainerFramingSawPattern;
import com.glodblock.github.extendedae.container.pattern.PatternGuiHandler;

public class FBCommonLoad {

    private static Class<?> PATTERN_CLASS;

    public static void init() {
        try {
            if (PATTERN_CLASS == null) {
                PATTERN_CLASS = Class.forName("xfacthd.framedblocks.common.compat.ae2.FramingSawPatternDetails");
            }
            PatternGuiHandler.addPatternHandler(PATTERN_CLASS, ContainerFramingSawPattern.ID);
        } catch (Throwable ignored) {
            // NO-OP
        }
    }

}
