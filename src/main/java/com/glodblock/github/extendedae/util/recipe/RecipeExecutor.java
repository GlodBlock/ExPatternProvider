package com.glodblock.github.extendedae.util.recipe;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.ticking.TickRateModulation;
import com.glodblock.github.extendedae.api.IRecipeMachine;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.function.Function;

public class RecipeExecutor<T extends Recipe<?>> {

    private final IRecipeMachine<?, T> machine;
    private final int maxTime;
    private final Function<T, ItemStack> outputGetter;

    public RecipeExecutor(IRecipeMachine<?, T> machine, Function<T, ItemStack> outputGetter, int maxTime) {
        this.machine = machine;
        this.maxTime = maxTime;
        this.outputGetter = outputGetter;
    }

    public TickRateModulation execute(int speed, boolean usePower) {
        var ctx = this.machine.getContext();
        var mainNode = this.machine.getNode();
        var power = this.machine.getEnergy();
        var runRecipe = ctx.currentRecipe;
        var output = this.machine.getOutput();
        if (runRecipe != null) {
            this.machine.setWorking(true);
            if (usePower) {
                if (mainNode != null) {
                    mainNode.ifPresent(grid -> {
                        IEnergyService eg = grid.getEnergyService();
                        IEnergySource src = power;
                        final int powerConsumption = 10 * speed;
                        final double powerThreshold = powerConsumption - 0.01;
                        double powerReq = 0;
                        if (src != null) {
                            powerReq = src.extractAEPower(powerConsumption, Actionable.SIMULATE, PowerMultiplier.CONFIG);
                        }
                        if (powerReq <= powerThreshold) {
                            src = eg;
                            powerReq = eg.extractAEPower(powerConsumption, Actionable.SIMULATE, PowerMultiplier.CONFIG);
                        }
                        if (src != null && powerReq > powerThreshold) {
                            src.extractAEPower(powerConsumption, Actionable.MODULATE, PowerMultiplier.CONFIG);
                            this.machine.addProgress(speed);
                        }
                    });
                }
            } else {
                this.machine.addProgress(speed);
            }
            if (this.machine.getProgress() >= this.maxTime) {
                this.machine.setProgress(0);
                var outputStack = this.outputGetter.apply(runRecipe).copy();
                if (ctx.testRecipe(runRecipe)) {
                    if (output.insertItem(0, outputStack, true).isEmpty()) {
                        ctx.runRecipe(runRecipe);
                        output.insertItem(0, outputStack, false);
                    } else {
                        ctx.stuck = true;
                    }
                }
                ctx.currentRecipe = null;
            }
            return TickRateModulation.URGENT;
        } else {
            if (ctx.shouldTick()) {
                ctx.findRecipe();
            }
            if (ctx.currentRecipe == null) {
                this.machine.setWorking(false);
            }
            return TickRateModulation.FASTER;
        }
    }

}