package com.glodblock.github.extendedae.datagen;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.api.ISpecialDrop;
import com.glodblock.github.extendedae.common.EAERegistryHandler;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.util.EPPTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class EPPBlockTagProvider extends BlockTagsProvider {

    public EPPBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ExtendedAE.MODID, existingFileHelper);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        TagKey<Block> pickaxe = BlockTags.MINEABLE_WITH_PICKAXE;
        for (var block : EAERegistryHandler.INSTANCE.getBlocks()) {
            if (!(block instanceof ISpecialDrop)) {
                tag(pickaxe).add(block);
            }
        }
        if (ModList.get().isLoaded("appliede")) {
            tag(pickaxe).addOptional(EPPItemAndBlock.EX_EMC_INTERFACE.getRegistryName());
        }
        tag(EPPTags.SILICON_BLOCK_BLOCK)
                .add(EPPItemAndBlock.SILICON_BLOCK);
        tag(Tags.Blocks.STORAGE_BLOCKS)
                .add(EPPItemAndBlock.SILICON_BLOCK);
    }
}
