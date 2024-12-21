package com.glodblock.github.extendedae.recipe.util;

import com.glodblock.github.extendedae.recipe.CircuitCutterRecipe;
import com.google.gson.JsonObject;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public interface MachineRecipe<T extends Recipe<?>> extends RecipeSerializer<CircuitCutterRecipe> {

    void toJson(JsonObject jsonObject, T recipe);

}
