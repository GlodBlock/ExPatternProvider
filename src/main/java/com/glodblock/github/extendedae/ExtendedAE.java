package com.glodblock.github.extendedae;

import com.glodblock.github.extendedae.client.ClientRegistryHandler;
import com.glodblock.github.extendedae.client.hotkey.PatternHotKey;
import com.glodblock.github.extendedae.common.EAERegistryHandler;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.hooks.CutterHook;
import com.glodblock.github.extendedae.config.EAEConfig;
import com.glodblock.github.extendedae.network.EAENetworkHandler;
import com.glodblock.github.extendedae.recipe.CrystalFixerRecipe;
import com.glodblock.github.extendedae.util.LazyInits;
import com.glodblock.github.extendedae.xmod.ModConstants;
import com.glodblock.github.extendedae.xmod.darkmode.BlacklistGUI;
import com.glodblock.github.extendedae.xmod.wt.ContainerWirelessExPAT;
import com.glodblock.github.extendedae.xmod.wt.HostWirelessExPAT;
import com.glodblock.github.glodium.util.GlodUtil;
import com.mojang.logging.LogUtils;
import de.mari_023.ae2wtlib.api.gui.Icon;
import de.mari_023.ae2wtlib.api.registration.AddTerminalEvent;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;

@Mod(ExtendedAE.MODID)
public class ExtendedAE {

    public static final String MODID = "extendedae";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static ExtendedAE INSTANCE;

    public ExtendedAE(IEventBus bus, ModContainer container) {
        assert INSTANCE == null;
        INSTANCE = this;
        if (!container.getModId().equals(MODID)) {
            throw new IllegalArgumentException("Invalid ID: " + MODID);
        }
        container.registerConfig(ModConfig.Type.COMMON, EAEConfig.SPEC);
        bus.addListener((RegisterEvent e) -> {
            if (e.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) {
                EAERegistryHandler.INSTANCE.registerTab(e.getRegistry(Registries.CREATIVE_MODE_TAB));
                return;
            }
            if (e.getRegistryKey().equals(Registries.BLOCK)) {
                EAESingletons.init(EAERegistryHandler.INSTANCE);
                EAERegistryHandler.INSTANCE.runRegister();
                return;
            }
            if (e.getRegistryKey().equals(Registries.ITEM)) {
                Icon.Texture TX = new Icon.Texture(id("textures/guis/nicons.png"), 64, 64);
                AddTerminalEvent.register(event -> event.builder(
                        "ex_pattern_access",
                        HostWirelessExPAT::new,
                        ContainerWirelessExPAT.TYPE,
                        EAESingletons.WIRELESS_EX_PAT,
                        new Icon(32, 32, 16, 16, TX)
                ).hotkeyName("wireless_pattern_access_terminal").translationKey("item.extendedae.wireless_ex_pat").addTerminal());
            }
        });
        if (FMLEnvironment.dist.isClient()) {
            bus.register(ClientRegistryHandler.INSTANCE);
            NeoForge.EVENT_BUS.addListener(this::onRecipeUpdate);
        }
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::sendIMC);
        bus.addListener(this::onFinalization);
        bus.addListener(EAENetworkHandler.INSTANCE::onRegister);
        bus.register(EAERegistryHandler.INSTANCE);
        NeoForge.EVENT_BUS.register(CutterHook.INSTANCE);
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        EAERegistryHandler.INSTANCE.onInit();
        LazyInits.initCommon();
    }

    public void clientSetup(FMLClientSetupEvent event) {
        PatternHotKey.onInit();
    }

    public void sendIMC(InterModEnqueueEvent event) {
        if (GlodUtil.checkMod(ModConstants.DARK_MODE)) {
            for (var method : BlacklistGUI.LIST) {
                InterModComms.sendTo(ModConstants.DARK_MODE, "dme-shaderblacklist", () -> method);
            }
        }
    }

    public void onRecipeUpdate(RecipesUpdatedEvent event) {
        CrystalFixerRecipe.clearLookup();
    }

    public void onFinalization(FMLLoadCompleteEvent event) {
        LazyInits.initFinal();
    }

    public static ResourceLocation id(String id) {
        return ResourceLocation.fromNamespaceAndPath(MODID, id);
    }

}
