package com.glodblock.github.extendedae.xmod.emi.recipes;

import appeng.core.AppEng;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.recipe.CrystalFixerRecipe;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import guideme.document.LytRect;
import guideme.render.SimpleRenderContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class EMICrystalFixerRecipe extends BasicEmiRecipe {

    public static final EmiRecipeCategory CATEGORY = new EAERecipeCategory("fixer", EmiStack.of(EAESingletons.CRYSTAL_FIXER), Component.translatable("emi.extendedae.category.fixer"));
    private static final DecimalFormat F = new DecimalFormat("#.#%", new DecimalFormatSymbols());
    private final CrystalFixerRecipe recipe;

    public EMICrystalFixerRecipe(RecipeHolder<CrystalFixerRecipe> holder) {
        super(CATEGORY, holder.id(), 114, 63);
        this.recipe = holder.value();
        this.inputs.add(EmiStack.of(this.recipe.getInput()));
        this.inputs.add(EMIStackUtil.of(this.recipe.getFuel()));
        this.outputs.add(EmiStack.of(this.recipe.getOutput()));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        ResourceLocation background = AppEng.makeId("textures/xei/crystal_fixer.png");
        widgets.addTexture(background, 0, 0, 114, 63, 0, 0);
        widgets.addDrawable(42, 29, 30, 30, (guiGraphics, mouseX, mouseY, delta) -> {
            var renderContext = new SimpleRenderContext(LytRect.empty(), guiGraphics);
            renderContext.renderItem(
                    new ItemStack(EAESingletons.CRYSTAL_FIXER),
                    0, 0,
                    30, 30);
        });
        widgets.addSlot(EmiStack.of(this.recipe.getInput()), 0, 18).drawBack(false);
        widgets.addSlot(EmiStack.of(this.recipe.getOutput()), 96, 18).recipeContext(this).drawBack(false);
        widgets.addSlot(EMIStackUtil.of(this.recipe.getFuel()), 48, 11).drawBack(false);
        widgets.addText(Component.translatable("emi.extendedae.text.success_chance", F.format(this.recipe.getChance())),
                1, 2, 0x7E7E7E, false);
    }

}
