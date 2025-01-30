package com.glodblock.github.extendedae.container;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.core.definitions.AEItems;
import appeng.crafting.pattern.AECraftingPattern;
import appeng.crafting.pattern.AEProcessingPattern;
import appeng.crafting.pattern.EncodedPatternItem;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.OutputSlot;
import appeng.menu.slot.RestrictedInputSlot;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.api.IPage;
import com.glodblock.github.extendedae.client.ExSemantics;
import com.glodblock.github.extendedae.client.gui.widget.SingleFakeSlot;
import com.glodblock.github.extendedae.common.me.itemhost.HostPatternModifier;
import com.glodblock.github.extendedae.util.Ae2Reflect;
import com.glodblock.github.glodium.network.packet.sync.ActionMap;
import com.glodblock.github.glodium.network.packet.sync.IActionHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ContainerPatternModifier extends AEBaseMenu implements IPage, IActionHolder {

    private final ActionMap actions = ActionMap.create();
    public static final MenuType<ContainerPatternModifier> TYPE = MenuTypeBuilder
            .create(ContainerPatternModifier::new, HostPatternModifier.class)
            .buildUnregistered(ExtendedAE.id("pattern_modifier"));

    public final AppEngSlot targetSlot;
    public final AppEngSlot cloneSlot;
    public final AppEngSlot replaceTarget;
    public final AppEngSlot replaceWith;
    @GuiSync(1)
    public int page;

    public ContainerPatternModifier(int id, Inventory playerInventory, HostPatternModifier host) {
        super(TYPE, id, playerInventory, host);
        this.createPlayerInventorySlots(playerInventory);
        var patternInv = host.getInventoryByName("patternInv");
        for (int x = 0; x < patternInv.size(); x ++) {
            this.addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.PROVIDER_PATTERN, patternInv, x), SlotSemantics.ENCODED_PATTERN);
        }
        var targetInv = host.getInventoryByName("targetInv");
        var blankPatternInv = host.getInventoryByName("blankPatternInv");
        var clonePatternInv = host.getInventoryByName("clonePatternInv");
        var replaceInv = host.getInventoryByName("replaceInv");
        this.targetSlot = (AppEngSlot) this.addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.PROVIDER_PATTERN, targetInv, 0), ExSemantics.EX_1);
        this.cloneSlot = (AppEngSlot) this.addSlot(new OutputSlot(clonePatternInv, 0, null), ExSemantics.EX_2);
        this.replaceTarget = (AppEngSlot) this.addSlot(new SingleFakeSlot(replaceInv, 0), ExSemantics.EX_4);
        this.replaceWith = (AppEngSlot) this.addSlot(new SingleFakeSlot(replaceInv, 1), ExSemantics.EX_5);
        for (int x = 0; x < blankPatternInv.size(); x ++) {
            this.addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.BLANK_PATTERN, blankPatternInv, x), ExSemantics.EX_3);
        }
        this.actions.put("clear", o -> clear());
        this.actions.put("clone", o -> clonePattern());
        this.actions.put("modify", o -> modify(o.get(0), o.get(1)));
        this.actions.put("replace", o -> replace());
        this.actions.put("show", o -> showPage());
    }

    public void showPage() {
        for (var slot : this.getSlots(SlotSemantics.ENCODED_PATTERN)) {
            if (slot instanceof AppEngSlot as) {
                as.setSlotEnabled(this.page == 0 || this.page == 1);
            }
        }
        this.replaceTarget.setSlotEnabled(this.page == 1);
        this.replaceWith.setSlotEnabled(this.page == 1);
        this.targetSlot.setSlotEnabled(this.page == 2);
        this.cloneSlot.setSlotEnabled(this.page == 2);
        for (var slot : this.getSlots(ExSemantics.EX_3)) {
            if (slot instanceof AppEngSlot as) {
                as.setSlotEnabled(this.page == 2);
            }
        }
    }

    public void replace() {
        var replace = this.replaceTarget.getItem();
        var with = this.replaceWith.getItem();
        if (replace.isEmpty() || with.isEmpty()) {
            return;
        }
        for (var slot : this.getSlots(SlotSemantics.ENCODED_PATTERN)) {
            var stack = slot.getItem();
            if (stack.getItem() instanceof EncodedPatternItem<?>) {
                var detail = PatternDetailsHelper.decodePattern(stack, this.getPlayer().level());
                if (detail instanceof AEProcessingPattern process) {
                    var input = process.getSparseInputs().toArray(new GenericStack[0]);
                    var output = process.getOutputs().toArray(new GenericStack[0]);
                    var replaceInput = new GenericStack[input.length];
                    var replaceOutput = new GenericStack[output.length];
                    this.replace(input, replaceInput, AEItemKey.of(replace), AEItemKey.of(with));
                    this.replace(output, replaceOutput, AEItemKey.of(replace), AEItemKey.of(with));
                    var newPattern = PatternDetailsHelper.encodeProcessingPattern(
                            Arrays.stream(replaceInput).toList(),
                            Arrays.stream(replaceOutput).toList()
                    );
                    slot.set(newPattern);
                } else if (detail instanceof AECraftingPattern craft) {
                    var input = craft.getSparseInputs().toArray(new GenericStack[0]);
                    var output = craft.getPrimaryOutput();
                    var replaceInput = new GenericStack[input.length];
                    this.replace(input, replaceInput, AEItemKey.of(replace), AEItemKey.of(with));
                    try {
                        var newPattern = PatternDetailsHelper.encodeCraftingPattern(
                                Ae2Reflect.getCraftRecipe(craft),
                                itemize(replaceInput),
                                itemize(output),
                                craft.canSubstitute,
                                craft.canSubstituteFluids
                        );
                        //noinspection DataFlowIssue
                        var check = new AECraftingPattern(AEItemKey.of(newPattern), this.getPlayer().level());
                        //noinspection ConstantValue
                        if (check != null) {
                            slot.set(newPattern);
                        }
                    } catch (Exception e) {
                        // It is an invalid change
                        return;
                    }
                }
            }
        }
    }

    public void clear() {
        for (var slot : this.getSlots(SlotSemantics.ENCODED_PATTERN)) {
            var stack = slot.getItem();
            if (stack.getItem() instanceof EncodedPatternItem) {
                slot.set(AEItems.BLANK_PATTERN.stack());
            }
        }
    }

    public void clonePattern() {
        var target = this.targetSlot.getItem();
        var clone = this.cloneSlot.getItem();
        if (target.getItem() instanceof EncodedPatternItem<?>) {
            var detail = PatternDetailsHelper.decodePattern(target, this.getPlayer().level());
            if (detail != null) {
                var newPattern = target.copy();
                if (clone.isEmpty()) {
                    if (consumeBlankPattern()) {
                        this.cloneSlot.set(newPattern);
                    }
                } else if (clone.getItem() instanceof EncodedPatternItem) {
                    this.cloneSlot.set(newPattern);
                }
            }
        }
    }

    public void modify(int scale, boolean div) {
        if (scale <= 0) {
            return;
        }
        for (var slot : this.getSlots(SlotSemantics.ENCODED_PATTERN)) {
            var stack = slot.getItem();
            if (stack.getItem() instanceof EncodedPatternItem<?>) {
                var detail = PatternDetailsHelper.decodePattern(stack, this.getPlayer().level());
                if (detail instanceof AEProcessingPattern process) {
                    var input = process.getSparseInputs().toArray(new GenericStack[0]);
                    var output = process.getOutputs().toArray(new GenericStack[0]);
                    if (checkModify(input, scale, div) && checkModify(output, scale, div)) {
                        var mulInput = new GenericStack[input.length];
                        var mulOutput = new GenericStack[output.length];
                        modifyStacks(input, mulInput, scale, div);
                        modifyStacks(output, mulOutput, scale, div);
                        var newPattern = PatternDetailsHelper.encodeProcessingPattern(
                                Arrays.stream(mulInput).toList(),
                                Arrays.stream(mulOutput).toList()
                        );
                        slot.set(newPattern);
                    }
                }
            }
        }
    }

    private boolean checkModify(GenericStack[] stacks, int scale, boolean div) {
        if (div) {
            for (var stack : stacks) {
                if (stack != null) {
                    if (stack.amount() % scale != 0) {
                        return false;
                    }
                }
            }
        } else {
            for (var stack : stacks) {
                if (stack != null) {
                    long upper = 999999L * stack.what().getAmountPerUnit();
                    if (stack.amount() * scale > upper) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void modifyStacks(GenericStack[] stacks, GenericStack[] des, int scale, boolean div) {
        for (int i = 0; i < stacks.length; i ++) {
            if (stacks[i] != null) {
                long amt = div ? stacks[i].amount() / scale : stacks[i].amount() * scale;
                des[i] = new GenericStack(stacks[i].what(), amt);
            }
        }
    }

    private void replace(GenericStack[] stacks, GenericStack[] des, AEKey replace, AEKey with) {
        for (int i = 0; i < stacks.length; i ++) {
            if (stacks[i] != null) {
                if (stacks[i].what().equals(replace)) {
                    des[i] = new GenericStack(with, stacks[i].amount());
                } else {
                    des[i] = new GenericStack(stacks[i].what(), stacks[i].amount());
                }
            }
        }
    }

    private ItemStack[] itemize(GenericStack[] stacks) {
        var items = new ItemStack[stacks.length];
        for (int i = 0; i < stacks.length; i ++) {
            items[i] = itemize(stacks[i]);
        }
        return items;
    }

    private ItemStack itemize(GenericStack stack) {
        if (stack != null && stack.what() instanceof AEItemKey what) {
            return what.toStack((int) stack.amount());
        } else {
            return ItemStack.EMPTY;
        }
    }

    private boolean consumeBlankPattern() {
        for (var slot : this.getSlots(ExSemantics.EX_3)) {
            var stack = slot.getItem();
            if (!stack.isEmpty() && AEItems.BLANK_PATTERN.is(stack)) {
                stack.shrink(1);
                if (stack.getCount() <= 0) {
                    slot.set(ItemStack.EMPTY);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public int getPage() {
        return this.page;
    }

    @NotNull
    @Override
    public ActionMap getActionMap() {
        return this.actions;
    }
}
