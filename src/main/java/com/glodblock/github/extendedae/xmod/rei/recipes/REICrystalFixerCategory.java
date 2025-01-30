package com.glodblock.github.extendedae.xmod.rei.recipes;

import appeng.core.AppEng;
import com.glodblock.github.extendedae.common.EAESingletons;
import guideme.document.LytRect;
import guideme.render.SimpleRenderContext;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class REICrystalFixerCategory implements DisplayCategory<REICrystalFixerDisplay> {

    private static final int PADDING = 5;
    private static final DecimalFormat F = new DecimalFormat("#.#%", new DecimalFormatSymbols());

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(EAESingletons.CRYSTAL_FIXER);
    }

    @Override
    public Component getTitle() {
        return EAESingletons.CRYSTAL_FIXER.getName();
    }

    @Override
    public CategoryIdentifier<REICrystalFixerDisplay> getCategoryIdentifier() {
        return REICrystalFixerDisplay.ID;
    }

    @Override
    public List<Widget> setupDisplay(REICrystalFixerDisplay recipeDisplay, Rectangle bounds) {
        ResourceLocation location = AppEng.makeId("textures/xei/crystal_fixer.png");

        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(location, bounds.x + PADDING, bounds.y + PADDING, 0, 0, 114, 63));
        var output = recipeDisplay.getOutputEntries().getFirst();
        var input = recipeDisplay.getInput();
        var fuel = recipeDisplay.getFuel();
        widgets.add(Widgets.createSlot(new Point(bounds.x + 1 + PADDING, bounds.y + 19 + PADDING)).disableBackground().markInput().entries(input));
        widgets.add(Widgets.createSlot(new Point(bounds.x + 97 + PADDING, bounds.y + 19 + PADDING)).disableBackground().markOutput().entries(output));
        widgets.add(Widgets.createSlot(new Point(bounds.x + 50 + PADDING, bounds.y + 12 + PADDING)).disableBackground().markInput().entries(fuel));
        widgets.add(Widgets.createDrawableWidget((guiGraphics, mouseX, mouseY, delta) -> {
            var renderContext = new SimpleRenderContext(LytRect.empty(), guiGraphics);
            renderContext.renderItem(
                    new ItemStack(EAESingletons.CRYSTAL_FIXER),
                    bounds.x + 43 + PADDING, bounds.y + 30 + PADDING,
                    30, 30);
        }));
        widgets.add(Widgets.createLabel(new Point(bounds.x + 58, bounds.y + 2 + PADDING), Component.translatable("emi.extendedae.text.success_chance", F.format(recipeDisplay.getChance())))
                .color(0x7E7E7E)
                .noShadow());
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 63 + 2 * PADDING;
    }

    @Override
    public int getDisplayWidth(REICrystalFixerDisplay display) {
        return 114 + 2 * PADDING;
    }

}
