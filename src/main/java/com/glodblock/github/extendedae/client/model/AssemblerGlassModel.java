package com.glodblock.github.extendedae.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class AssemblerGlassModel implements IUnbakedGeometry<AssemblerGlassModel> {

    @Override
    public @NotNull BakedModel bake(@NotNull IGeometryBakingContext context, @NotNull ModelBaker loader, @NotNull Function<Material, TextureAtlasSprite> textureGetter, @NotNull ModelState rotationContainer, @NotNull ItemOverrides overrides) {
        return new AssemblerGlassBakedModel(textureGetter);
    }

    public static class Loader implements IGeometryLoader<AssemblerGlassModel> {

        @Override
        public @NotNull AssemblerGlassModel read(@NotNull JsonObject jsonObject, @NotNull JsonDeserializationContext deserializationContext) throws JsonParseException {
            return new AssemblerGlassModel();
        }

    }

}
