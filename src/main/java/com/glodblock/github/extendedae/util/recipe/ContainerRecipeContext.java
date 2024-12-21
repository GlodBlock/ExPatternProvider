package com.glodblock.github.extendedae.util.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public abstract class ContainerRecipeContext<T extends Recipe<Container>> extends RecipeSearchContext<Container, T> {

    public ContainerRecipeContext(Supplier<Level> levelGetter, RecipeType<T> type) {
        super(levelGetter, type);
    }

}