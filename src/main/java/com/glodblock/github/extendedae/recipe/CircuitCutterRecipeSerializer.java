package com.glodblock.github.extendedae.recipe;

import com.glodblock.github.extendedae.recipe.util.IngredientStack;
import com.glodblock.github.extendedae.recipe.util.MachineRecipe;
import com.glodblock.github.extendedae.recipe.util.MachineResult;
import com.glodblock.github.extendedae.recipe.util.StackJson;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CircuitCutterRecipeSerializer implements MachineRecipe<CircuitCutterRecipe> {

    public final static CircuitCutterRecipeSerializer INSTANCE = new CircuitCutterRecipeSerializer();
    public final static MachineResult.Type<CircuitCutterRecipe> RESULT = MachineResult.Type.type(INSTANCE);

    private CircuitCutterRecipeSerializer() {
        // NO-OP
    }

    @Override
    public void toJson(JsonObject json, CircuitCutterRecipe recipe) {
        json.add("item_input", recipe.input.toJson());
        if (!recipe.fluid.isEmpty()) {
            json.add("fluid_input", recipe.fluid.toJson());
        }
        json.add("output", StackJson.toJson(recipe.output));
    }

    @Override
    public @NotNull CircuitCutterRecipe fromJson(@NotNull ResourceLocation id, @NotNull JsonObject json) {
        var input = IngredientStack.ofItem(json.getAsJsonObject("item_input"));
        var fluid = IngredientStack.Fluid.EMPTY;
        if (json.has("fluid_input")) {
            fluid = IngredientStack.ofFluid(json.getAsJsonObject("fluid_input"));
        }
        var output = StackJson.fromJson(json.get("output"));
        return new CircuitCutterRecipe(id, output, input, fluid);
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull CircuitCutterRecipe recipe) {
        buffer.writeItem(recipe.output);
        recipe.input.to(buffer);
        recipe.fluid.to(buffer);
    }

    @Override
    public @Nullable CircuitCutterRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull FriendlyByteBuf buff) {
        var output = buff.readItem();
        var input = IngredientStack.ofItem(buff);
        var fluid = IngredientStack.ofFluid(buff);
        return new CircuitCutterRecipe(id, output, input, fluid);
    }

}