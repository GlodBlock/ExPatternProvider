package com.glodblock.github.extendedae.container;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.api.VoidMode;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.items.ItemVoidCell;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.SEAEGenericPacket;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerVoidCell extends AEBaseMenu implements IActionHolder {

    public static final MenuType<ContainerVoidCell> TYPE = MenuTypeBuilder
            .create(ContainerVoidCell::new, ItemMenuHost.class)
            .buildUnregistered(ExtendedAE.id("void_cell"));
    private final ActionMap actions = ActionMap.create();
    private final ItemStack stack;
    @GuiSync(1)
    public int mode = 0;

    public ContainerVoidCell(int id, Inventory playerInventory, ItemMenuHost<ItemVoidCell> host) {
        super(TYPE, id, playerInventory, host);
        this.stack = host.getItemStack();
        this.actions.put("set", o -> this.setMode(o.get(0)));
        this.actions.put("update", o -> {
            if (this.getPlayer() instanceof ServerPlayer sp) {
                EAENetworkHandler.INSTANCE.sendTo(new SEAEGenericPacket("init", this.mode), sp);
            }
        });
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        var stackMode = this.stack.getOrDefault(EAESingletons.VOID_MODE, VoidMode.TRASH);
        this.mode = stackMode.ordinal();
    }

    public void setMode(int mode) {
        this.stack.set(EAESingletons.VOID_MODE, VoidMode.values()[mode]);
        this.broadcastChanges();
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }
}
