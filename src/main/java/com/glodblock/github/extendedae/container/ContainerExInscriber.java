package com.glodblock.github.extendedae.container;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.util.IConfigManager;
import appeng.blockentity.misc.InscriberRecipes;
import appeng.client.gui.Icon;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.ItemDefinition;
import appeng.core.localization.Side;
import appeng.core.localization.Tooltips;
import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.interfaces.IProgressProvider;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.OutputSlot;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.api.IPage;
import com.glodblock.github.extendedae.client.ExSemantics;
import com.glodblock.github.extendedae.common.tileentities.TileExInscriber;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ContainerExInscriber extends UpgradeableMenu<TileExInscriber> implements IProgressProvider, IPage, IActionHolder {

    public static final MenuType<ContainerExInscriber> TYPE = MenuTypeBuilder
            .create(ContainerExInscriber::new, TileExInscriber.class)
            .buildUnregistered(ExtendedAE.id("ex_inscriber"));

    private final Slot[] tops = new Slot[4];
    private final Slot[] middles = new Slot[4];
    private final Slot[] bottoms = new Slot[4];
    private final Slot[] outputs = new Slot[4];
    private final ActionMap actions = ActionMap.create();
    private static final SlotSemantic[] TOP = {
            SlotSemantics.INSCRIBER_PLATE_TOP,
            ExSemantics.EX_1,
            ExSemantics.EX_2,
            ExSemantics.EX_3,
    };
    private static final SlotSemantic[] BOTTOM = {
            SlotSemantics.INSCRIBER_PLATE_BOTTOM,
            ExSemantics.EX_4,
            ExSemantics.EX_5,
            ExSemantics.EX_6,
    };
    private static final SlotSemantic[] MIDDLE = {
            SlotSemantics.MACHINE_INPUT,
            ExSemantics.EX_7,
            ExSemantics.EX_8,
            ExSemantics.EX_9,
    };
    private static final SlotSemantic[] OUTPUT = {
            SlotSemantics.MACHINE_OUTPUT,
            ExSemantics.EX_10,
            ExSemantics.EX_11,
            ExSemantics.EX_12,
    };

    @GuiSync(2)
    public int maxProcessingTime = -1;

    @GuiSync(3)
    public int processingTime = -1;

    @GuiSync(7)
    public YesNo separateSides = YesNo.NO;
    @GuiSync(8)
    public YesNo autoExport = YesNo.NO;
    @GuiSync(9)
    public int page = 0;

    public ContainerExInscriber(int id, Inventory ip, TileExInscriber host) {
        super(TYPE, id, ip, host);
        this.actions.put("show", o -> showPage());
        this.actions.put("stack", o -> this.getHost().setInvStackSize(o.get(0)));
        for (int x = 0; x < TileExInscriber.MAX_THREAD; x ++) {
            var inv = host.getIndexInventory(x);

            var top = new AppEngSlot(inv, 0);
            top.setIcon(Icon.BACKGROUND_PLATE);
            top.setEmptyTooltip(
                    () -> separateSides == YesNo.YES ? Tooltips.inputSlot(Side.TOP) : Tooltips.inputSlot(Side.ANY));
            this.tops[x] = this.addSlot(top, TOP[x]);

            var bottom = new AppEngSlot(inv, 1);
            bottom.setIcon(Icon.BACKGROUND_PLATE);
            bottom.setEmptyTooltip(
                    () -> separateSides == YesNo.YES ? Tooltips.inputSlot(Side.BOTTOM) : Tooltips.inputSlot(Side.ANY));
            this.bottoms[x] = this.addSlot(bottom, BOTTOM[x]);

            var middle = new AppEngSlot(inv, 2);
            middle.setIcon(Icon.BACKGROUND_INGOT);
            middle.setEmptyTooltip(
                    () -> separateSides == YesNo.YES ? Tooltips.inputSlot(Side.LEFT, Side.RIGHT, Side.BACK, Side.FRONT)
                            : Tooltips.inputSlot(Side.ANY));
            this.middles[x] = this.addSlot(middle, MIDDLE[x]);

            var output = new OutputSlot(inv, 3, null);
            output.setEmptyTooltip(
                    () -> separateSides == YesNo.YES ? Tooltips.outputSlot(Side.LEFT, Side.RIGHT, Side.BACK, Side.FRONT)
                            : Tooltips.outputSlot(Side.ANY));
            this.outputs[x] = this.addSlot(output, OUTPUT[x]);
        }
    }

    public void showPage() {
        for (int x = 0; x < TileExInscriber.MAX_THREAD; x ++) {
            ((AppEngSlot) this.tops[x]).setSlotEnabled(x == this.page);
            ((AppEngSlot) this.bottoms[x]).setSlotEnabled(x == this.page);
            ((AppEngSlot) this.middles[x]).setSlotEnabled(x == this.page);
            ((AppEngSlot) this.outputs[x]).setSlotEnabled(x == this.page);
        }
        this.getHost().markForUpdate();
    }

    public int getStackMode() {
        return switch (this.getHost().getInvStackSize()) {
            case 64 -> 0;
            case 1 -> 1;
            case 4 -> 2;
            default -> throw new IllegalStateException("Unexpected stack size: " + this.getHost().getInvStackSize());
        };
    }

    @Override
    protected void loadSettingsFromHost(IConfigManager cm) {
        this.separateSides = getHost().getConfigManager().getSetting(Settings.INSCRIBER_SEPARATE_SIDES);
        this.autoExport = getHost().getConfigManager().getSetting(Settings.AUTO_EXPORT);
    }

    @Override
    protected void standardDetectAndSendChanges() {
        if (isServerSide()) {
            this.maxProcessingTime = getHost().getMaxProcessingTime();
            this.processingTime = getHost().getProcessingTime(this.page);
        }
        super.standardDetectAndSendChanges();
    }

    @Override
    public boolean isValidForSlot(Slot s, ItemStack is) {
        final ItemStack top = getHost().getInternalInventory().getStackInSlot(0);
        final ItemStack bot = getHost().getInternalInventory().getStackInSlot(1);

        if (s == this.middles[this.page]) {
            ItemDefinition<?> press = AEItems.NAME_PRESS;
            if (press.is(top) || press.is(bot)) {
                return !press.is(is);
            }

            return InscriberRecipes.findRecipe(getHost().getLevel(), is, top, bot, false) != null;
        } else if (s == this.tops[this.page] && !bot.isEmpty() || s == this.bottoms[this.page] && !top.isEmpty()) {
            ItemStack otherSlot;
            if (s == this.tops[this.page]) {
                otherSlot = this.bottoms[this.page].getItem();
            } else {
                otherSlot = this.tops[this.page].getItem();
            }

            // name presses
            ItemDefinition<?> namePress = AEItems.NAME_PRESS;
            if (namePress.is(otherSlot)) {
                return namePress.is(is);
            }

            // everything else
            // test for a partial recipe match (ignoring the middle slot)
            return InscriberRecipes.isValidOptionalIngredientCombination(Objects.requireNonNull(getHost().getLevel()), is, otherSlot);
        }
        return true;
    }

    @Override
    public int getCurrentProgress() {
        return this.processingTime;
    }

    @Override
    public int getMaxProgress() {
        return this.maxProcessingTime;
    }

    public YesNo getSeparateSides() {
        return separateSides;
    }

    public YesNo getAutoExport() {
        return autoExport;
    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public int getPage() {
        return this.page;
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }
}
