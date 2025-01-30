package com.glodblock.github.extendedae.common.tileentities;

import appeng.api.stacks.AEKeyType;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.helpers.externalstorage.GenericStackInv;
import com.glodblock.github.extendedae.api.caps.IGenericInvHost;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.xmod.ExternalTypes;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class TileIngredientBuffer extends AEBaseBlockEntity implements IGenericInvHost {

    private final GenericStackInv buffer;

    public TileIngredientBuffer(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileIngredientBuffer.class, TileIngredientBuffer::new, EAESingletons.INGREDIENT_BUFFER), pos, blockState);
        this.buffer = new GenericStackInv(this::setChanged, 36);
        this.buffer.setCapacity(AEKeyType.fluids(), 64000);
        if (ExternalTypes.GAS != null) {
            this.buffer.setCapacity(ExternalTypes.GAS, 64000);
        }
        if (ExternalTypes.MANA != null) {
            this.buffer.setCapacity(ExternalTypes.MANA, 1000);
        }
        // Disable FE
        if (ExternalTypes.FLUX != null) {
            this.buffer.setCapacity(ExternalTypes.FLUX, 0);
        }
        if (ExternalTypes.SOURCE != null) {
            this.buffer.setCapacity(ExternalTypes.SOURCE, 1000);
        }
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (int index = 0; index < this.buffer.size(); index ++) {
            var stack = this.buffer.getStack(index);
            if (stack != null) {
                stack.what().addDrops(stack.amount(), drops, level, pos);
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries) {
        super.saveAdditional(data, registries);
        this.buffer.writeToChildTag(data, "buffer", registries);
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.buffer.clear();
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries) {
        super.loadTag(data, registries);
        this.buffer.readFromChildTag(data, "buffer", registries);
    }

    @Override
    public GenericStackInv getGenericInv() {
        return this.buffer;
    }

}
