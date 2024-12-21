package com.glodblock.github.extendedae.recipe;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.recipe.util.IngredientStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CircuitCutterRecipe implements Recipe<Container> {

    public static final ResourceLocation TYPE_ID = ExtendedAE.id("circuit_cutter");
    public static final RecipeType<CircuitCutterRecipe> TYPE = RecipeType.simple(TYPE_ID);

    protected final IngredientStack.Item input;
    protected final IngredientStack.Fluid fluid;
    protected final ResourceLocation id;
    public final ItemStack output;

    public CircuitCutterRecipe(ResourceLocation id, ItemStack output, IngredientStack.Item input, IngredientStack.Fluid fluid) {
        this.id = id;
        this.output = output;
        this.input = input;
        this.fluid = fluid;
    }

    public IngredientStack.Item getInput() {
        return this.input;
    }

    public IngredientStack.Fluid getFluid() {
        return this.fluid;
    }

    public List<IngredientStack<?>> getSample() {
        var list = new ArrayList<IngredientStack<?>>();
        list.add(this.input.sample());
        if (!this.fluid.isEmpty()) {
            list.add(this.fluid.sample());
        }
        return list;
    }

    @Override
    public boolean matches(@NotNull Container pContainer, @NotNull Level pLevel) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container pContainer, @NotNull RegistryAccess pRegistryAccess) {
        return getResultItem(pRegistryAccess).copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess pRegistryAccess) {
        return this.output;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return CircuitCutterRecipeSerializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @NotNull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

}