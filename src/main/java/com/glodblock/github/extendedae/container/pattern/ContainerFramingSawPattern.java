package com.glodblock.github.extendedae.container.pattern;

import appeng.api.stacks.GenericStack;
import com.glodblock.github.extendedae.ExtendedAE;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ContainerFramingSawPattern extends ContainerPattern {

    public static final ResourceLocation ID = ExtendedAE.id("framing_saw");
    public static final MenuType<ContainerFramingSawPattern> TYPE = PatternGuiHandler.register(ID, ContainerFramingSawPattern::new);

    public ContainerFramingSawPattern(@Nullable MenuType<?> menuType, int id, Level world, ItemStack stack) {
        super(TYPE, world, id, stack);
        this.addSlot(new DisplayOnlySlot(this, this.inputs, 0, 52, 15));
        for (int index = 0; index < 3; index ++) {
            this.addSlot(new DisplayOnlySlot(this, this.inputs, index + 1, 34 + index * 18, 34));
        }
        this.addSlot(new DisplayOnlySlot(this, this.outputs, 0, 124, 25));
    }

    @Override
    protected void analyse() {
        try {
            var rawInputs = this.details.getInputs();
            var rawOutputs = this.details.getPrimaryOutput();
            for (var input : rawInputs) {
                var in = clean(input.getPossibleInputs());
                var inStacks = new GenericStack[in.length];
                for (int j = 0; j < inStacks.length; j ++) {
                    inStacks[j] = new GenericStack(in[j].what(), in[j].amount());
                }
                this.inputs.add(inStacks);
            }
            this.outputs.add(new GenericStack[] {new GenericStack(rawOutputs.what(), rawOutputs.amount())});
        } catch (Throwable t) {
            this.invalidate();
        }
    }
}
