package com.glodblock.github.extendedae.container;

import appeng.blockentity.AEBaseBlockEntity;
import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.parts.AEBasePart;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.network.packet.SEAEGenericPacket;
import com.glodblock.github.extendedae.util.Ae2Reflect;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ContainerRenamer extends AEBaseMenu implements IActionHolder {

    public static final MenuType<ContainerRenamer> TYPE = MenuTypeBuilder
            .create(ContainerRenamer::new, Object.class)
            .buildUnregistered(ExtendedAE.id("renamer"));
    private final ActionMap actions = ActionMap.create();
    private final Consumer<String> setter;
    private final Supplier<Component> getter;
    @GuiSync(1)
    public String name = "";

    public ContainerRenamer(int id, Inventory playerInventory, Object host) {
        super(TYPE, id, playerInventory, host);
        this.getter = getter(host);
        this.setter = setter(host);
        if (this.setter == null || this.getter == null) {
            this.setValidMenu(false);
        }
        this.actions.put("set", o -> this.setName(o.get(0)));
        this.actions.put("update", o -> {
            if (this.getPlayer() instanceof ServerPlayer sp) {
                EAENetworkHandler.INSTANCE.sendTo(new SEAEGenericPacket("init", this.name), sp);
            }
        });
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        var newName = this.getter.get() == null ? "" : this.getter.get().getString();
        if (!this.name.equals(newName)) {
            this.name = newName;
        }
    }

    public void setName(String name) {
        if (name != null && !name.isEmpty()) {
            this.setter.accept(name);
        } else {
            this.setter.accept("");
        }
        broadcastChanges();
    }

    private static Supplier<Component> getter(Object o) {
        if (o instanceof Nameable n) {
            return n::getCustomName;
        }
        return null;
    }

    private static Consumer<String> setter(Object o) {
        if (o instanceof AEBaseBlockEntity || o instanceof AEBasePart) {
            return s -> {
                var c = s.isBlank() ? null : Component.literal(s);
                Ae2Reflect.setCustomName(o, c);
            };
        }
        return null;
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }
}
