package com.glodblock.github.extendedae.xmod.jei.recipe;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.recipe.CircuitCutterRecipe;
import com.glodblock.github.extendedae.xmod.jei.util.StackUtil;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CircuitCutterCategory implements IRecipeCategory<CircuitCutterRecipe> {

    public static RecipeType<CircuitCutterRecipe> RECIPE_TYPE = RecipeType.create(ExtendedAE.MODID, "circuit_cutter", CircuitCutterRecipe.class);
    private final IDrawable background;
    private final IDrawableAnimated progress;
    private final IDrawable icon;

    public CircuitCutterCategory(IGuiHelper helpers) {
        ResourceLocation location = new ResourceLocation("ae2", "textures/guis/circuit_cutter.png");
        this.background = helpers.createDrawable(location, 27, 32, 110, 26);
        IDrawableStatic progressDrawable = helpers.drawableBuilder(location, 178, 36, 6, 18).addPadding(4, 0, 104, 0).build();
        this.progress = helpers.createAnimatedDrawable(progressDrawable, 40, IDrawableAnimated.StartDirection.BOTTOM, false);
        this.icon = helpers.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(EPPItemAndBlock.CIRCUIT_CUTTER));
    }

    @Override
    public @NotNull RecipeType<CircuitCutterRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return EPPItemAndBlock.CIRCUIT_CUTTER.getName();
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull CircuitCutterRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 5).setSlotName("item_input").addIngredients(StackUtil.of(recipe.getInput()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 82, 5).setSlotName("output").addItemStack(recipe.output);
        if (!recipe.getFluid().isEmpty()) {
            var tank = builder.addSlot(RecipeIngredientRole.INPUT, 19, 5).setSlotName("fluid_input").setFluidRenderer(1, false, 16, 16);
            StackUtil.addFluid(recipe.getFluid(), tank);
        }
    }

    @Override
    public void draw(@NotNull CircuitCutterRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.progress.draw(guiGraphics);
    }

}
