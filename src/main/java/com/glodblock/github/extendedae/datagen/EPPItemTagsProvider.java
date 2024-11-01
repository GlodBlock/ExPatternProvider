package com.glodblock.github.extendedae.datagen;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.util.EPPTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EPPItemTagsProvider extends ItemTagsProvider {

    public EPPItemTagsProvider(PackOutput p, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> block, @Nullable ExistingFileHelper existingFileHelper) {
        super(p, lookupProvider, block, ExtendedAE.MODID, existingFileHelper);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(EPPTags.EX_PATTERN_PROVIDER)
                .add(EPPItemAndBlock.EX_PATTERN_PROVIDER_PART)
                .add(EPPItemAndBlock.EX_PATTERN_PROVIDER.asItem());
        tag(EPPTags.EX_INTERFACE)
                .add(EPPItemAndBlock.EX_INTERFACE_PART)
                .add(EPPItemAndBlock.EX_INTERFACE.asItem());
        tag(EPPTags.OVERSIZE_INTERFACE)
                .add(EPPItemAndBlock.OVERSIZE_INTERFACE_PART)
                .add(EPPItemAndBlock.OVERSIZE_INTERFACE.asItem());
        if (ModList.get().isLoaded("appliede")) {
            tag(EPPTags.EX_EMC_INTERFACE)
                    .addOptional(EPPItemAndBlock.EX_EMC_INTERFACE.getRegistryName())
                    .addOptional(EPPItemAndBlock.EX_EMC_INTERFACE_PART.getRegistryName());
        }
    }
}
