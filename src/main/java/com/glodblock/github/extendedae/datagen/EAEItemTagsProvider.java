package com.glodblock.github.extendedae.datagen;

import appeng.datagen.providers.tags.ConventionTags;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.util.EAETags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EAEItemTagsProvider extends ItemTagsProvider {

    public EAEItemTagsProvider(PackOutput p, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> block, @Nullable ExistingFileHelper existingFileHelper) {
        super(p, lookupProvider, block, ExtendedAE.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(EAETags.EX_PATTERN_PROVIDER)
                .add(EAESingletons.EX_PATTERN_PROVIDER_PART)
                .add(EAESingletons.EX_PATTERN_PROVIDER.asItem());
        tag(EAETags.EX_INTERFACE)
                .add(EAESingletons.EX_INTERFACE_PART)
                .add(EAESingletons.EX_INTERFACE.asItem());
        tag(EAETags.OVERSIZE_INTERFACE)
                .add(EAESingletons.OVERSIZE_INTERFACE_PART)
                .add(EAESingletons.OVERSIZE_INTERFACE.asItem());
        tag(Tags.Items.DUSTS)
                .add(EAESingletons.ENTRO_DUST)
                .add(EAESingletons.QUARTZ_BLEND);
        tag(Tags.Items.GEMS)
                .add(EAESingletons.ENTRO_CRYSTAL);
        tag(EAETags.ENTRO_CRYSTAL)
                .add(EAESingletons.ENTRO_CRYSTAL);
        tag(EAETags.ENTRO_DUST)
                .add(EAESingletons.ENTRO_DUST);
        tag(Tags.Items.INGOTS)
                .add(EAESingletons.ENTRO_INGOT);
        tag(EAETags.ENTRO_INGOT)
                .add(EAESingletons.ENTRO_INGOT);
        tag(Tags.Items.STORAGE_BLOCKS)
                .add(EAESingletons.ENTRO_BLOCK.asItem())
                .add(EAESingletons.SILICON_BLOCK.asItem());
        tag(EAETags.ENTRO_BLOCK)
                .add(EAESingletons.ENTRO_BLOCK.asItem());
        tag(ConventionTags.BUDDING_BLOCKS)
                .add(EAESingletons.FULLY_ENTROIZED_FLUIX_BUDDING.asItem())
                .add(EAESingletons.MOSTLY_ENTROIZED_FLUIX_BUDDING.asItem())
                .add(EAESingletons.HALF_ENTROIZED_FLUIX_BUDDING.asItem())
                .add(EAESingletons.HARDLY_ENTROIZED_FLUIX_BUDDING.asItem());
        tag(ConventionTags.BUDS)
                .add(EAESingletons.ENTRO_BUD_SMALL.asItem())
                .add(EAESingletons.ENTRO_BUD_MEDIUM.asItem())
                .add(EAESingletons.ENTRO_BUD_LARGE.asItem());
        tag(EAETags.SILICON_BLOCK)
                .add(EAESingletons.SILICON_BLOCK.asItem());
    }
}
