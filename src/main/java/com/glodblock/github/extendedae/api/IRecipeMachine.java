package com.glodblock.github.extendedae.api;

import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.energy.IEnergySource;
import com.glodblock.github.extendedae.util.recipe.RecipeSearchContext;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.Nullable;

public interface IRecipeMachine<C extends Container, T extends Recipe<C>> {

    int getProgress();

    void addProgress(int delta);

    void setProgress(int progress);

    RecipeSearchContext<C, T> getContext();

    void setWorking(boolean work);

    InternalInventory getOutput();

    @Nullable
    default IManagedGridNode getNode() {
        return null;
    }

    @Nullable
    default IEnergySource getEnergy() {
        return null;
    }

}