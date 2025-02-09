package com.glodblock.github.extendedae.xmod.framedblocks;

import com.glodblock.github.extendedae.container.pattern.ContainerFramingSawPattern;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class FBRegister {

    public static void register() {
        Registry.register(BuiltInRegistries.MENU, ContainerFramingSawPattern.ID, ContainerFramingSawPattern.TYPE);
    }

}
