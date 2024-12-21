package com.glodblock.github.extendedae.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Collections;
import java.util.Map;

public interface RecipeManagerAccessor {

    <C extends Container, T extends Recipe<C>> Map<ResourceLocation, T> getByType(RecipeType<T> type);

}
