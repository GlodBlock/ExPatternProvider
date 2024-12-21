package com.glodblock.github.extendedae.mixins;

import com.glodblock.github.extendedae.util.RecipeManagerAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(RecipeManager.class)
public abstract class MixinRecipeManager implements RecipeManagerAccessor {

    @Shadow
    protected abstract <C extends Container, T extends Recipe<C>> Map<ResourceLocation, T> byType(RecipeType<T> type);

    public <C extends Container, T extends Recipe<C>> Map<ResourceLocation, T> getByType(RecipeType<T> type) {
        return this.byType(type);
    }

}
