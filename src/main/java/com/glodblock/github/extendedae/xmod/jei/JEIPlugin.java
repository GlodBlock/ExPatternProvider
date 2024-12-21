package com.glodblock.github.extendedae.xmod.jei;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.client.gui.AEBaseScreen;
import appeng.integration.modules.jei.ChargerCategory;
import appeng.integration.modules.jei.GenericEntryStackHelper;
import appeng.items.misc.WrappedGenericStack;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.client.gui.GuiCircuitCutter;
import com.glodblock.github.extendedae.client.gui.GuiExInscriber;
import com.glodblock.github.extendedae.client.gui.pattern.GuiPattern;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.container.pattern.ContainerPattern;
import com.glodblock.github.extendedae.recipe.CircuitCutterRecipe;
import com.glodblock.github.extendedae.util.Ae2ReflectClient;
import com.glodblock.github.extendedae.util.RecipeManagerAccessor;
import com.glodblock.github.extendedae.xmod.jei.recipe.CircuitCutterCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IClickableIngredient;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    private IJeiRuntime jeiRuntime;

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ExtendedAE.id("jei_plugin");
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        this.jeiRuntime = jeiRuntime;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        var helpers = registry.getJeiHelpers();
        registry.addRecipeCategories(new CircuitCutterCategory(helpers.getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var manager = ((RecipeManagerAccessor) Minecraft.getInstance().level.getRecipeManager());
        registration.addRecipes(CircuitCutterCategory.RECIPE_TYPE, List.copyOf(manager.getByType(CircuitCutterRecipe.TYPE).values()));
    }

    @Override
    public void registerGuiHandlers(@NotNull IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(GuiPattern.class,
                new IGuiContainerHandler<GuiPattern<?>>() {
                    @Override
                    public @NotNull Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(@NotNull GuiPattern<?> screen, double mouseX, double mouseY) {
                        var stackWithBounds = screen.getSlotUnderMouse();
                        if (stackWithBounds instanceof ContainerPattern.DisplayOnlySlot dpSlot) {
                            var genStack = dpSlot.getItem();
                            if (!genStack.isEmpty()) {
                                var item = genStack.getItem();
                                var key = item instanceof WrappedGenericStack wgs
                                        ? wgs.unwrapWhat(genStack) : AEItemKey.of(genStack);
                                var amount = item instanceof WrappedGenericStack wgs
                                        ? wgs.unwrapAmount(genStack) : dpSlot.getActualAmount();
                                if (key != null && amount > 0) {
                                    var ing = GenericEntryStackHelper.stackToIngredient(jeiRuntime.getIngredientManager(), new GenericStack(key, amount));
                                    var area = new Rect2i(screen.getGuiLeft() + dpSlot.x, screen.getGuiTop() + dpSlot.y, 16, 16);
                                    if (ing != null) {
                                        return Optional.of(new IClickableIngredient<>() {
                                            @Override
                                            @SuppressWarnings({"rawtypes", "unchecked"})
                                            public @NotNull ITypedIngredient getTypedIngredient() {
                                                return ing;
                                            }

                                            @Override
                                            public @NotNull Rect2i getArea() {
                                                return area;
                                            }
                                        });
                                    }
                                }
                            }
                        }
                        return Optional.empty();
                    }
                }
        );
        registration.addGenericGuiContainerHandler(AEBaseScreen.class,
                new IGuiContainerHandler<AEBaseScreen<?>>() {
                    @Override
                    public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(@NotNull AEBaseScreen<?> screen, double mouseX, double mouseY) {
                        if (screen instanceof GuiExInscriber) {
                            return Collections.singletonList(
                                    IGuiClickableArea.createBasic(82, 50, 26, 16, Ae2ReflectClient.getInscribeRecipe())
                            );
                        }
                        if (screen instanceof GuiCircuitCutter) {
                            return Collections.singletonList(
                                    IGuiClickableArea.createBasic(65, 36, 37, 16, CircuitCutterCategory.RECIPE_TYPE)
                            );
                        }
                        return Collections.emptyList();
                    }
                }
        );
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(EPPItemAndBlock.EX_INSCRIBER), Ae2ReflectClient.getInscribeRecipe());
        registration.addRecipeCatalyst(new ItemStack(EPPItemAndBlock.EX_CHARGER), ChargerCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(EPPItemAndBlock.CIRCUIT_CUTTER), CircuitCutterCategory.RECIPE_TYPE);
    }

    @Override
    public void registerItemSubtypes(@NotNull ISubtypeRegistration registration) {
        registration.useNbtForSubtypes(EPPItemAndBlock.INFINITY_CELL);
    }

}
