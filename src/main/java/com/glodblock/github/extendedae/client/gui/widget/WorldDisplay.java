package com.glodblock.github.extendedae.client.gui.widget;

import appeng.client.gui.AEBaseScreen;
import appeng.client.guidebook.document.LytRect;
import appeng.client.guidebook.scene.CameraSettings;
import appeng.client.guidebook.scene.GuidebookLevelRenderer;
import appeng.client.guidebook.scene.GuidebookScene;
import appeng.client.guidebook.scene.level.GuidebookLevel;
import com.glodblock.github.extendedae.util.Ae2ReflectClient;
import com.glodblock.github.glodium.util.GlodUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

// Reuse AE guide's fake world stuff
public class WorldDisplay extends AbstractWidget {

    private final AEBaseScreen<?> addedOn;
    private float zoom = 2.0f;
    private GuidebookScene scene;
    private boolean ready;
    private boolean hideNeighbor;
    @NotNull
    private final static Level clientWorld;
    private final static GuidebookLevelRenderer worldRender = GuidebookLevelRenderer.getInstance();
    private LytRect bounds;

    static {
        assert Minecraft.getInstance().level != null;
        clientWorld = Minecraft.getInstance().level;
    }

    public WorldDisplay(AEBaseScreen<?> addedOn, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.addedOn = addedOn;
        this.ready = false;
        this.hideNeighbor = false;
    }

    public void unload() {
        this.ready = false;
    }

    public void setHideNeighbor(boolean val) {
        this.hideNeighbor = val;
    }

    public void locate(BlockPos blockPos) {
        // holy crap what shit i just made
        // TODO: 2023/8/25 optimize it later 
        this.zoom = 2.0f;
        this.ready = false;
        var block = clientWorld.getBlockState(blockPos);
        var te = clientWorld.getBlockEntity(blockPos);
        if (block.isAir() || te == null) {
            return;
        }
        this.scene = new GuidebookScene(new GuidebookLevel(), new CameraSettings());
        var wrap = Ae2ReflectClient.getFakeServerWorld(this.scene.getLevel());
        assert wrap != null;

        var sizeX = new Vec3i(3, 1, 1);
        var sizeY = new Vec3i(1, 3, 1);
        var sizeZ = new Vec3i(1, 1, 3);
        var startX = BlockPos.ZERO.offset(0, 1, 1);
        var startY = BlockPos.ZERO.offset(1, 0, 1);
        var startZ = BlockPos.ZERO.offset(1, 1, 0);
        var tmp = new StructureTemplate();
        var settings = new StructurePlaceSettings();
        var random = new SingleThreadedRandomSource(0L);
        settings.setIgnoreEntities(true);
        if (this.hideNeighbor) {
            tmp.fillFromWorld(clientWorld, blockPos, new Vec3i(1, 1, 1), false, Blocks.AIR);
            tmp.placeInWorld(wrap, new BlockPos(1, 1, 1), BlockPos.ZERO, settings, random, 0);
        } else {
            tmp.fillFromWorld(clientWorld, blockPos.offset(-1, 0, 0), sizeX, false, Blocks.AIR);
            tmp.placeInWorld(wrap, startX, BlockPos.ZERO, settings, random, 0);
            tmp = new StructureTemplate();
            tmp.fillFromWorld(clientWorld, blockPos.offset(0, -1, 0), sizeY, false, Blocks.AIR);
            tmp.placeInWorld(wrap, startY, BlockPos.ZERO, settings, random, 0);
            tmp = new StructureTemplate();
            tmp.fillFromWorld(clientWorld, blockPos.offset(0, 0, -1), sizeZ, false, Blocks.AIR);
            tmp.placeInWorld(wrap, startZ, BlockPos.ZERO, settings, random, 0);
        }
        this.scene.getCameraSettings().setRotationCenter(this.scene.getWorldCenter());
        this.scene.getCameraSettings().setZoom(this.zoom);
        this.bounds = new LytRect(getX(), getY(), this.width, this.height);
        this.scene.getCameraSettings().setViewportSize(this.bounds.size());
        this.scene.centerScene();
        this.ready = true;
    }

    public void refreshBounds() {
        this.bounds = new LytRect(getX(), getY(), this.width, this.height);
    }

    @Override
    public void playDownSound(@NotNull SoundManager manager) {
        // NO-OP
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.ready) {
            if (isMouseOver(mouseX, mouseY)) {
                this.addedOn.setFocused(this);
            }
            var window = Minecraft.getInstance().getWindow();
            // transform the viewport into physical screen coordinates
            var viewport = this.bounds.transform(guiGraphics.pose().last().pose());
            RenderSystem.viewport(
                    (int) (viewport.x() * window.getGuiScale()),
                    (int) (window.getHeight() - viewport.bottom() * window.getGuiScale()),
                    (int) (viewport.width() * window.getGuiScale()),
                    (int) (viewport.height() * window.getGuiScale())
            );
            guiGraphics.enableScissor(getX(), getY(), getX() + width, getY() + height);
            worldRender.render(this.scene.getLevel(), this.scene.getCameraSettings(), Collections.emptyList());
            guiGraphics.disableScissor();
            RenderSystem.viewport(0, 0, window.getWidth(), window.getHeight());
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput pNarrationElementOutput) {

    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (this.visible && this.ready && this.isMouseOver(pMouseX, pMouseY)) {
            float dx = (float) pDragX;
            float dy = (float) pDragY;
            var camera = this.scene.getCameraSettings();
            if (pButton == 0) {
                camera.setRotationY(camera.getRotationY() + dx);
                camera.setRotationX(camera.getRotationX() + dy);
            } else if (pButton == 1) {
                camera.setOffsetX(camera.getOffsetX() + dx);
                camera.setOffsetY(camera.getOffsetY() - dy);
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        if (this.visible && this.ready && this.isMouseOver(pMouseX, pMouseY)) {
            this.zoom = (float) GlodUtil.clamp(this.zoom + pScrollY / 5, 0.5, 10);
            this.scene.getCameraSettings().setZoom(this.zoom);
            return true;
        }
        return false;
    }

}
