package com.glodblock.github.extendedae.container;

import appeng.api.config.Settings;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.util.IConfigManager;
import appeng.core.definitions.AEItems;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.slot.FakeSlot;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.parts.PartThresholdLevelEmitter;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class ContainerThresholdLevelEmitter extends UpgradeableMenu<PartThresholdLevelEmitter>  {

    private static final String ACTION_SET_UPPER_VALUE = "setUpperValue";
    private static final String ACTION_SET_LOWER_VALUE = "setLowerValue";
    public static final MenuType<ContainerThresholdLevelEmitter> TYPE = MenuTypeBuilder
            .create(ContainerThresholdLevelEmitter::new, PartThresholdLevelEmitter.class)
            .withInitialData((host, buffer) -> {
                GenericStack.writeBuffer(host.getConfig().getStack(0), buffer);
                buffer.writeVarLong(host.getUpperValue());
                buffer.writeVarLong(host.getLowerValue());
            }, (host, menu, buffer) -> {
                menu.getHost().getConfig().setStack(0, GenericStack.readBuffer(buffer));
                menu.upperValue = buffer.readVarLong();
                menu.lowerValue = buffer.readVarLong();
            })
            .buildUnregistered(ExtendedAE.id("threshold_level_emitter"));

    @GuiSync(7)
    public long upperValue;
    @GuiSync(8)
    public long lowerValue;

    public ContainerThresholdLevelEmitter(int id, Inventory ip, PartThresholdLevelEmitter host) {
        super(TYPE, id, ip, host);
        registerClientAction(ACTION_SET_UPPER_VALUE, Long.class, this::setUpperValue);
        registerClientAction(ACTION_SET_LOWER_VALUE, Long.class, this::setLowerValue);
    }

    @Override
    protected void loadSettingsFromHost(IConfigManager cm) {
        if (cm.hasSetting(Settings.FUZZY_MODE)) {
            this.setFuzzyMode(cm.getSetting(Settings.FUZZY_MODE));
        }
        this.setRedStoneMode(cm.getSetting(Settings.REDSTONE_EMITTER));
    }

    public boolean supportsFuzzySearch() {
        return getHost().getConfigManager().hasSetting(Settings.FUZZY_MODE) && hasUpgrade(AEItems.FUZZY_CARD);
    }

    @Nullable
    public AEKey getConfiguredFilter() {
        return getHost().getConfig().getKey(0);
    }

    @Override
    protected void setupConfig() {
        var inv = getHost().getConfig().createMenuWrapper();
        this.addSlot(new FakeSlot(inv, 0), SlotSemantics.CONFIG);
    }

    public void setUpperValue(long value) {
        if (isClientSide()) {
            if (value != this.upperValue) {
                this.upperValue = value;
                sendClientAction(ACTION_SET_UPPER_VALUE, value);
            }
        } else {
            getHost().setUpperValue(value);
        }
    }

    public void setLowerValue(long value) {
        if (isClientSide()) {
            if (value != this.lowerValue) {
                this.lowerValue = value;
                sendClientAction(ACTION_SET_LOWER_VALUE, value);
            }
        } else {
            getHost().setLowerValue(value);
        }
    }

}
