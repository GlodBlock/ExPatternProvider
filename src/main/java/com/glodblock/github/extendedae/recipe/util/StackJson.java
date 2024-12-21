package com.glodblock.github.extendedae.recipe.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("deprecation")
public class StackJson {

    public static JsonObject toJson(ItemStack stack) {
        var json = new JsonObject();
        if (!stack.isEmpty()) {
            json.addProperty("item", BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
            if (stack.getCount() > 1) {
                json.addProperty("count", stack.getCount());
            }
        }
        return json;
    }

    public static ItemStack fromJson(JsonElement json) {
        var stack = ItemStack.EMPTY;
        if (json != null && json.isJsonObject()) {
            var obj = (JsonObject) json;
            if (obj.has("item")) {
                var item = BuiltInRegistries.ITEM.get(new ResourceLocation(obj.get("item").getAsString()));
                var count = 1;
                if (obj.has("count")) {
                    count = obj.get("count").getAsInt();
                }
                stack = new ItemStack(item, count);
            }
        }
        return stack;
    }

}
