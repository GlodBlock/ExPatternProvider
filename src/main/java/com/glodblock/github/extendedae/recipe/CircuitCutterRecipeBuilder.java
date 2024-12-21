package com.glodblock.github.extendedae.recipe;

import com.glodblock.github.extendedae.recipe.util.FluidIngredient;
import com.glodblock.github.extendedae.recipe.util.IngredientStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

public class CircuitCutterRecipeBuilder {

    protected IngredientStack.Item input = IngredientStack.Item.EMPTY;
    protected IngredientStack.Fluid fluid = IngredientStack.Fluid.EMPTY;
    public ItemStack output;

    public CircuitCutterRecipeBuilder(ItemStack output) {
        this.output = output.copy();
    }

    public static CircuitCutterRecipeBuilder cut(ItemStack stack) {
        return new CircuitCutterRecipeBuilder(stack);
    }

    public static CircuitCutterRecipeBuilder cut(ItemLike stack) {
        return new CircuitCutterRecipeBuilder(new ItemStack(stack));
    }

    public static CircuitCutterRecipeBuilder cut(ItemLike stack, int count) {
        return new CircuitCutterRecipeBuilder(new ItemStack(stack, count));
    }

    public CircuitCutterRecipeBuilder input(ItemStack item) {
        this.input = IngredientStack.of(item);
        return this;
    }

    public CircuitCutterRecipeBuilder input(ItemLike item) {
        this.input = IngredientStack.of(new ItemStack(item));
        return this;
    }

    public CircuitCutterRecipeBuilder input(ItemLike item, int count) {
        this.input = IngredientStack.of(new ItemStack(item, count));
        return this;
    }

    public CircuitCutterRecipeBuilder input(TagKey<Item> tag) {
        this.input = IngredientStack.of(Ingredient.of(tag), 1);
        return this;
    }

    public CircuitCutterRecipeBuilder input(TagKey<Item> tag, int count) {
        this.input = IngredientStack.of(Ingredient.of(tag), count);
        return this;
    }

    public CircuitCutterRecipeBuilder fluid(TagKey<Fluid> tag, int count) {
        this.fluid = IngredientStack.of(FluidIngredient.of(tag), count);
        return this;
    }

    public CircuitCutterRecipeBuilder fluid(Fluid fluid, int count) {
        this.fluid = IngredientStack.of(FluidIngredient.of(new FluidStack(fluid, 1)), count);
        return this;
    }

    public CircuitCutterRecipeBuilder fluid(FluidStack fluid) {
        this.fluid = IngredientStack.of(FluidIngredient.of(fluid), fluid.getAmount());
        return this;
    }

    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        var recipe = new CircuitCutterRecipe(id, this.output, this.input, this.fluid);
        consumer.accept(CircuitCutterRecipeSerializer.RESULT.result(id, recipe));
    }

    public void save(Consumer<FinishedRecipe> consumer) {
        var id = BuiltInRegistries.ITEM.getKey(this.output.getItem());
        var recipe = new CircuitCutterRecipe(id, this.output, this.input, this.fluid);
        consumer.accept(CircuitCutterRecipeSerializer.RESULT.result(id, recipe));
    }

}
