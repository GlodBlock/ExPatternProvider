package com.glodblock.github.extendedae.recipe.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("deprecation")
public class FluidIngredient implements Predicate<FluidStack> {

    private final List<Fluid> fluids = new ArrayList<>();
    protected final Value value;

    public List<Fluid> getFluid() {
        return this.fluids;
    }

    public static FluidIngredient of(FriendlyByteBuf buff) {
        var type = buff.readByte();
        if (type == 0) {
            return new FluidIngredient(new FluidValue(buff.readFluidStack()));
        } else if (type == 1) {
            return new FluidIngredient(new TagValue(TagKey.create(Registries.FLUID, buff.readResourceLocation())));
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static FluidIngredient of(JsonElement json) {
        if (json != null && json.isJsonObject()) {
            var obj = (JsonObject) json;
            if (obj.has("fluid")) {
                var fluid = BuiltInRegistries.FLUID.get(new ResourceLocation(obj.get("fluid").getAsString()));
                return new FluidIngredient(new FluidValue(new FluidStack(fluid, 1000)));
            } else if (obj.has("tag")) {
                var tag = new ResourceLocation(obj.get("tag").getAsString());
                return new FluidIngredient(new TagValue(TagKey.create(Registries.FLUID, tag)));
            }
        }
        return new FluidIngredient(new FluidValue(FluidStack.EMPTY));
    }

    public static FluidIngredient of(FluidStack fluid) {
        return new FluidIngredient(new FluidValue(fluid));
    }

    public static FluidIngredient of(TagKey<Fluid> fluid) {
        return new FluidIngredient(new TagValue(fluid));
    }

    public JsonElement toJson() {
        var json = new JsonObject();
        if (this.value instanceof FluidValue f) {
            json.addProperty("fluid", BuiltInRegistries.FLUID.getKey(f.fluid.getFluid()).toString());
        } else if (this.value instanceof TagValue f) {
            json.addProperty("tag", f.fluid.location().toString());
        }
        return json;
    }

    public void to(FriendlyByteBuf buff) {
        if (this.value instanceof FluidValue f) {
            buff.writeByte(0);
            buff.writeFluidStack(f.fluid);
        } else if (this.value instanceof TagValue f) {
            buff.writeByte(1);
            buff.writeResourceLocation(f.fluid.location());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public FluidIngredient(Value v) {
        this.value = v;
        if (v instanceof FluidValue fluid) {
            if (!fluid.fluid.isEmpty()) {
                this.fluids.add(fluid.fluid.getFluid());
            }
        } else if (v instanceof TagValue fluid) {
            for (var holder : BuiltInRegistries.FLUID.getTagOrEmpty(fluid.fluid)) {
                this.fluids.add(holder.value());
            }
        }
    }

    @Override
    public boolean test(FluidStack fluidStack) {
        if (this.fluids.isEmpty()) {
            return fluidStack.isEmpty();
        }
        if (fluidStack.isEmpty()) {
            return false;
        }
        var fluid = fluidStack.getFluid();
        for (var tf : this.fluids) {
            if (tf.isSame(fluid)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        if (this.value instanceof TagValue f) {
            return "tag: " + f.fluid;
        }
        if (this.value instanceof FluidValue f) {
            return "fluid: " + f.fluid;
        }
        return super.toString();
    }

    public interface Value {

    }

    public record TagValue(TagKey<Fluid> fluid) implements Value {
    }

    public record FluidValue(FluidStack fluid) implements Value {
    }

}