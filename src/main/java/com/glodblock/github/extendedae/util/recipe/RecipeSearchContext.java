package com.glodblock.github.extendedae.util.recipe;

import com.glodblock.github.extendedae.util.RecipeManagerAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public abstract class RecipeSearchContext<C extends Container, T extends Recipe<C>> {

    public boolean stuck;
    public boolean dirty;
    @Nullable
    public T lastRecipe;
    @Nullable
    public T currentRecipe;
    private final Supplier<Level> levelGetter;
    private final RecipeType<T> type;

    public RecipeSearchContext(Supplier<Level> levelGetter, RecipeType<T> type) {
        this.levelGetter = levelGetter;
        this.type = type;
    }

    public void findRecipe() {
        if (lastRecipe != null) {
            if (testRecipe(lastRecipe)) {
                currentRecipe = lastRecipe;
                stuck = false;
                return;
            }
            lastRecipe = null;
        }
        stuck = false;
        this.onFind(this.searchRecipe());
    }

    public void onInvChange() {
        stuck = false;
        dirty = true;
    }

    public boolean shouldTick() {
        if (currentRecipe != null) {
            return true;
        }
        return !stuck;
    }

    public void onFind(@Nullable T recipe) {
        if (recipe == null) {
            if (dirty) {
                dirty = false;
                return;
            }
            stuck = true;
            currentRecipe = null;
            return;
        }
        dirty = false;
        lastRecipe = recipe;
        currentRecipe = recipe;
        stuck = false;
    }

    public T searchRecipe() {
        var level = this.levelGetter.get();
        if (level == null) {
            return null;
        }
        var recipes = this.getRecipeMap(level);
        for (var recipe : recipes.values()) {
            if (testRecipe(recipe)) {
                return recipe;
            }
        }
        return null;
    }

    public abstract boolean testRecipe(T recipe);

    public abstract void runRecipe(T recipe);

    public void save(CompoundTag tag) {
        var nbt = new CompoundTag();
        if (this.currentRecipe != null) {
            nbt.putString("current", this.currentRecipe.getId().toString());
        }
        if (this.lastRecipe != null) {
            nbt.putString("last", this.lastRecipe.getId().toString());
        }
        tag.put("recipeCtx", nbt);
    }

    public void load(CompoundTag tag) {
        var level = this.levelGetter.get();
        if (level == null) {
            return;
        }
        var nbt = tag.getCompound("recipeCtx");
        if (nbt.contains("current")) {
            try {
                var id = new ResourceLocation(tag.getString("current"));
                this.currentRecipe = this.getRecipeMap(level).get(id);
            } catch (Throwable e) {
                this.currentRecipe = null;
            }
        }
        if (nbt.contains("last")) {
            try {
                var id = new ResourceLocation(tag.getString("last"));
                this.lastRecipe = this.getRecipeMap(level).get(id);
            } catch (Throwable e) {
                this.lastRecipe = null;
            }
        }
    }

    private Map<ResourceLocation, T> getRecipeMap(Level world) {
        return ((RecipeManagerAccessor) world.getRecipeManager()).getByType(this.type);
    }

}