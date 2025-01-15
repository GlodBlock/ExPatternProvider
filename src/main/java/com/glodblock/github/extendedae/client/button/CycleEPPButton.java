package com.glodblock.github.extendedae.client.button;

import appeng.client.gui.Icon;
import appeng.client.gui.style.Blitter;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CycleEPPButton extends EPPButton {

    private final ArrayList<Blitter> icons = new ArrayList<>();
    private final ArrayList<Component> messages = new ArrayList<>();
    private final ArrayList<OnPress> actions = new ArrayList<>();
    private int state;
    private int size = 0;

    public CycleEPPButton() {
        super(b -> {});
    }


    public void addActionPair(Icon icon, Component tooltip, OnPress action) {
        this.addActionPair(icon.getBlitter(), tooltip, action);
    }

    public void addActionPair(Blitter icon, Component tooltip, OnPress action) {
        this.icons.add(icon);
        this.messages.add(tooltip);
        this.actions.add(action);
        this.size ++;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    @Override
    Blitter getBlitterIcon() {
        return this.icons.get(this.state);
    }

    @Override
    public @NotNull Component getMessage() {
        return this.messages.get(this.state);
    }

    @Override
    public void setMessage(@NotNull Component tooltip) {
        this.messages.set(this.state, tooltip);
    }

    @Override
    public void onPress() {
        this.actions.get(this.state).onPress(this);
        this.state = (this.state + 1) % this.size;
    }

}
