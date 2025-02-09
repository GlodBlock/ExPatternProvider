package com.glodblock.github.extendedae.xmod.framedblocks;

import com.glodblock.github.extendedae.client.gui.pattern.GuiFramingSawPattern;
import com.glodblock.github.extendedae.container.pattern.ContainerFramingSawPattern;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class FBClientLoad {

    public static void init(RegisterMenuScreensEvent event) {
        event.register(ContainerFramingSawPattern.TYPE, GuiFramingSawPattern::new);
    }

}
