package com.glodblock.github.extendedae.network;

import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.network.packet.CPatternKey;
import com.glodblock.github.extendedae.network.packet.CUpdatePage;
import com.glodblock.github.extendedae.network.packet.SAssemblerAnimation;
import com.glodblock.github.extendedae.network.packet.SAssemblerMatrixUpdate;
import com.glodblock.github.extendedae.network.packet.SExPatternInfo;
import com.glodblock.github.glodium.network.NetworkHandler;
import com.glodblock.github.glodium.network.packet.CGenericPacket;
import com.glodblock.github.glodium.network.packet.SGenericPacket;

public class EPPNetworkHandler extends NetworkHandler {

    public static final EPPNetworkHandler INSTANCE = new EPPNetworkHandler();

    public EPPNetworkHandler() {
        super(ExtendedAE.MODID);
    }

    public void init() {
        registerPacket(SExPatternInfo.class, SExPatternInfo::new);
        registerPacket(SAssemblerAnimation.class, SAssemblerAnimation::new);
        registerPacket(SAssemblerMatrixUpdate.class, SAssemblerMatrixUpdate::new);
        registerPacket(SGenericPacket.class, SGenericPacket::new);
        registerPacket(CPatternKey.class, CPatternKey::new);
        registerPacket(CUpdatePage.class, CUpdatePage::new);
        registerPacket(CGenericPacket.class, CGenericPacket::new);
    }

}
