package com.glodblock.github.extendedae.container;

import appeng.api.config.Settings;
import appeng.api.util.IConfigManager;
import appeng.helpers.InterfaceLogicHost;
import appeng.menu.SlotSemantic;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.SetStockAmountMenu;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.FakeSlot;
import com.glodblock.github.extendedae.api.IPage;
import com.glodblock.github.extendedae.client.ExSemantics;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

public class ContainerExInterface extends UpgradeableMenu<InterfaceLogicHost> implements IPage {

    public static final String ACTION_OPEN_SET_AMOUNT = "setAmount";

    public static final MenuType<ContainerExInterface> TYPE = MenuTypeBuilder
            .create(ContainerExInterface::new, InterfaceLogicHost.class)
            .build("ex_interface");
    public static final MenuType<ContainerExInterface> TYPE_OVERSIZE = MenuTypeBuilder
            .create(ContainerExInterface::new, InterfaceLogicHost.class)
            .build("oversize_interface");

    private static final int PAGE = 18;
    private static final int LINE = 9;
    private static final SlotSemantic[] CONFIG_PATTERN = new SlotSemantic[] {
            ExSemantics.EX_1, ExSemantics.EX_3, ExSemantics.EX_5, ExSemantics.EX_7
    };
    private static final SlotSemantic[] STORAGE_PATTERN = new SlotSemantic[] {
            ExSemantics.EX_2, ExSemantics.EX_4, ExSemantics.EX_6, ExSemantics.EX_8
    };
    private final List<Slot> configSlots = new ArrayList<>();
    @GuiSync(7)
    public int page;

    public ContainerExInterface(MenuType<?> menuType, int id, Inventory ip, InterfaceLogicHost host) {
        super(menuType, id, ip, host);
        registerClientAction(ACTION_OPEN_SET_AMOUNT, Integer.class, this::openSetAmountMenu);
        var logic = host.getInterfaceLogic();
        var config = logic.getConfig().createMenuWrapper();
        for (int x = 0; x < config.size(); x++) {
            int page = x / PAGE;
            int row = (x - page * PAGE) / LINE;
            this.configSlots.add(this.addSlot(new FakeSlot(config, x), CONFIG_PATTERN[2 * page + row]));
        }
        var storage = logic.getStorage().createMenuWrapper();
        for (int x = 0; x < storage.size(); x++) {
            int page = x / PAGE;
            int row = (x - page * PAGE) / LINE;
            this.addSlot(new AppEngSlot(storage, x), STORAGE_PATTERN[2 * page + row]);
        }
    }

    public List<Slot> getConfigSlots() {
        return this.configSlots;
    }

    public void showPage(int page) {
        for (int index = 0; index < 4; index ++) {
            var slots = this.getSlots(CONFIG_PATTERN[index]);
            slots.addAll(this.getSlots(STORAGE_PATTERN[index]));
            for (var slot : slots) {
                if (slot instanceof AppEngSlot as) {
                    as.setActive(page == (index / 2));
                }
            }
        }
    }

    @Override
    protected void loadSettingsFromHost(IConfigManager cm) {
        this.setFuzzyMode(cm.getSetting(Settings.FUZZY_MODE));
    }

    public void openSetAmountMenu(int configSlot) {
        if (isClientSide()) {
            sendClientAction(ACTION_OPEN_SET_AMOUNT, configSlot);
        } else {
            var stack = getHost().getConfig().getStack(configSlot);
            if (stack != null) {
                SetStockAmountMenu.open((ServerPlayer) getPlayer(), getLocator(), configSlot,
                        stack.what(), (int) stack.amount());
            }
        }
    }

    @Override
    public void broadcastChanges() {
        if (this.getHost() instanceof IPage pg) {
            this.page = pg.getPage();
        }
        super.broadcastChanges();
    }

    @Override
    public void setPage(int page) {
        this.page = page;
        if (this.getHost() instanceof IPage pg) {
            pg.setPage(page);
        }
    }

    @Override
    public int getPage() {
        return this.page;
    }
}
