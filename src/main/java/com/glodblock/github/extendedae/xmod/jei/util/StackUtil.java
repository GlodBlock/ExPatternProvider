package com.glodblock.github.extendedae.xmod.jei.util;

import com.glodblock.github.extendedae.recipe.util.FluidIngredient;
import com.glodblock.github.extendedae.recipe.util.IngredientStack;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import net.minecraft.world.item.crafting.Ingredient;

public class StackUtil {

    public static Ingredient of(IngredientStack.Item stack) {
        return (Ingredient) stack.getIngredient();
    }

    public static void addFluid(IngredientStack.Fluid stack, IRecipeSlotBuilder slot) {
        for (var fluid : ((FluidIngredient) stack.getIngredient()).getFluid()) {
            slot.addFluidStack(fluid, stack.getAmount());
        }
    }

}
