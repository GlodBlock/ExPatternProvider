package com.glodblock.github.extendedae.recipe.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Predicate;

public abstract class IngredientStack<T> {

    protected final Predicate<T> ingredient;
    protected int amount;

    public IngredientStack(Predicate<T> ingredient, int amount) {
        this.ingredient = ingredient;
        this.amount = amount;
    }

    public Predicate<T> getIngredient() {
        return this.ingredient;
    }

    public int getAmount() {
        return this.amount;
    }

    public static IngredientStack.Item of(ItemStack ingredient) {
        return new Item(Ingredient.of(ingredient), ingredient.getCount());
    }

    public static IngredientStack.Item of(Ingredient ingredient, int amount) {
        return new Item(ingredient, amount);
    }

    public static IngredientStack.Fluid of(FluidStack ingredient) {
        return new Fluid(new FluidIngredient(new FluidIngredient.FluidValue(ingredient)), ingredient.getAmount());
    }

    public static IngredientStack.Fluid of(FluidIngredient ingredient, int amount) {
        return new Fluid(ingredient, amount);
    }

    public static IngredientStack.Item ofItem(FriendlyByteBuf buff) {
        return new Item(Ingredient.fromNetwork(buff), buff.readInt());
    }

    public static IngredientStack.Fluid ofFluid(FriendlyByteBuf buff) {
        return new Fluid(FluidIngredient.of(buff), buff.readInt());
    }

    public static IngredientStack.Item ofItem(JsonObject json) {
        return new Item(json.get("ingredient"), json.get("amount").getAsInt());
    }

    public static IngredientStack.Fluid ofFluid(JsonObject json) {
        return new Fluid(json.get("ingredient"), json.get("amount").getAsInt());
    }

    public abstract void to(FriendlyByteBuf buff);

    public abstract JsonElement toJson();

    @SuppressWarnings("unchecked")
    public void consume(Object stack) {
        if (this.amount <= 0) {
            return;
        }
        if (this.ingredient.test((T) stack)) {
            int from = getStackAmount((T) stack);
            if (from > this.amount) {
                this.setStackAmount((T) stack, from - this.amount);
                this.amount = 0;

            } else {
                this.setStackAmount((T) stack, 0);
                this.amount -= from;
            }
        }
    }

    public boolean isEmpty() {
        return this.amount <= 0;
    }

    public abstract boolean checkType(Object obj);

    public abstract IngredientStack<T> sample();

    public abstract int getStackAmount(T stack);

    public abstract void setStackAmount(T stack, int amount);

    @Override
    public String toString() {
        return this.amount + " " +this.ingredient;
    }

    public static final class Item extends IngredientStack<ItemStack> {

        public static final Item EMPTY = new Item(Ingredient.EMPTY, 0);

        private Item(JsonElement ingredient, int amount) {
            super(Ingredient.fromJson(ingredient), amount);
        }

        private Item(Ingredient ingredient, int amount) {
            super(ingredient, amount);
        }

        @Override
        public void to(FriendlyByteBuf buff) {
            ((Ingredient) this.ingredient).toNetwork(buff);
            buff.writeInt(this.amount);
        }

        @Override
        public JsonElement toJson() {
            var json = new JsonObject();
            json.add("ingredient", ((Ingredient) this.ingredient).toJson());
            json.addProperty("amount", this.amount);
            return json;
        }

        @Override
        public boolean checkType(Object obj) {
            return obj instanceof ItemStack;
        }

        @Override
        public Item sample() {
            return new Item((Ingredient) this.ingredient, this.amount);
        }

        @Override
        public int getStackAmount(ItemStack stack) {
            return stack.getCount();
        }

        @Override
        public void setStackAmount(ItemStack stack, int amount) {
            stack.setCount(amount);
        }

    }

    public static final class Fluid extends IngredientStack<FluidStack> {

        public static final Fluid EMPTY = new Fluid(new FluidIngredient(new FluidIngredient.FluidValue(FluidStack.EMPTY)), 0);

        private Fluid(FluidIngredient ingredient, int amount) {
            super(ingredient, amount);
        }

        private Fluid(JsonElement ingredient, int amount) {
            super(FluidIngredient.of(ingredient), amount);
        }

        @Override
        public void to(FriendlyByteBuf buff) {
            ((FluidIngredient) this.ingredient).to(buff);
            buff.writeInt(this.amount);
        }

        @Override
        public JsonElement toJson() {
            var json = new JsonObject();
            json.add("ingredient", ((FluidIngredient) this.ingredient).toJson());
            json.addProperty("amount", this.amount);
            return json;
        }

        @Override
        public boolean checkType(Object obj) {
            return obj instanceof FluidStack;
        }

        @Override
        public IngredientStack<FluidStack> sample() {
            return new Fluid((FluidIngredient) this.ingredient, this.amount);
        }

        @Override
        public int getStackAmount(FluidStack stack) {
            return stack.getAmount();
        }

        @Override
        public void setStackAmount(FluidStack stack, int amount) {
            stack.setAmount(amount);
        }
    }

}