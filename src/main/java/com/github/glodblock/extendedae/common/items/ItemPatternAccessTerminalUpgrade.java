package com.github.glodblock.extendedae.common.items;

import appeng.parts.reporting.PatternAccessTerminalPart;
import com.github.glodblock.extendedae.common.EAEItemAndBlock;
import net.minecraft.world.item.Item;

public class ItemPatternAccessTerminalUpgrade extends ItemUpgrade {

    public ItemPatternAccessTerminalUpgrade() {
        super(new Item.Properties());
        this.addPart(PatternAccessTerminalPart.class, EAEItemAndBlock.EX_PATTERN_TERMINAL);
    }

}
