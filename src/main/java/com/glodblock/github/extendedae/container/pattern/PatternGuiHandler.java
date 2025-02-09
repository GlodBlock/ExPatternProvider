package com.glodblock.github.extendedae.container.pattern;

import appeng.api.crafting.IPatternDetails;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

import java.util.IdentityHashMap;

public class PatternGuiHandler {

    private static final Object2ObjectMap<ResourceLocation, IContainerPattern> factory = new Object2ObjectOpenHashMap<>();
    private static final Object2ReferenceMap<ResourceLocation, MenuType<?>> types = new Object2ReferenceOpenHashMap<>();
    private static final IdentityHashMap<Class<?>, ResourceLocation> patternID = new IdentityHashMap<>();
    private static final BiMap<Integer, ResourceLocation> internal = HashBiMap.create();
    private static int IDZ = 0;

    public static void addPatternHandler(Class<?> clazz, ResourceLocation id) {
        patternID.put(clazz, id);
    }

    public static boolean open(Player player, IPatternDetails details, ItemStack pattern) {
        var cls = details.getClass();
        if (patternID.containsKey(cls)) {
            open(player, patternID.get(cls), pattern);
            return true;
        } else {
            return false;
        }
    }

    public static void open(Player player, ResourceLocation id, ItemStack pattern) {
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        var title = Component.translatable("epp.pattern." + id);
        MenuProvider menu = new SimpleMenuProvider((wnd, p, pl) -> {
            var f = factory.get(id);
            var t = types.get(id);
            return f.create(t, wnd, player.level(), pattern);
        }, title);
        player.openMenu(menu, buffer -> to(id, pattern, buffer));
    }

    private static AbstractContainerMenu from(int containerId, Inventory inv, RegistryFriendlyByteBuf packetBuf) {
        var id = packetBuf.readVarInt();
        var world = inv.player.level();
        var stack = ItemStack.STREAM_CODEC.decode(packetBuf);
        var f = factory.get(internal.get(id));
        var t = types.get(internal.get(id));
        return f.create(t, containerId, world, stack);
    }

    private static void to(ResourceLocation id, ItemStack pattern, RegistryFriendlyByteBuf packetBuf) {
        packetBuf.writeVarInt(internal.inverse().get(id));
        ItemStack.STREAM_CODEC.encode(packetBuf, pattern);
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractContainerMenu> MenuType<T> register(ResourceLocation id, IContainerPattern containerFactory) {
        factory.put(id, containerFactory);
        internal.put(IDZ, id);
        IDZ++;
        var type = IMenuTypeExtension.create(PatternGuiHandler::from);
        types.put(id, type);
        return (MenuType<T>) type;
    }

    @FunctionalInterface
    public interface IContainerPattern {

        ContainerPattern create(MenuType<?> menuType, int id, Level world, ItemStack pattern);

    }

}
