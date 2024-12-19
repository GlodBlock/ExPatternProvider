package com.glodblock.github.extendedae.common.me.taglist;

import appeng.api.stacks.AEKey;
import appeng.util.prioritylist.IPartitionList;
import it.unimi.dsi.fastutil.objects.Reference2BooleanMap;
import it.unimi.dsi.fastutil.objects.Reference2BooleanOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public class TagPriorityList implements IPartitionList {

    private static final Map<TagPriorityList, Runnable> INVALIDATOR = new WeakHashMap<>();
    private Set<TagKey<?>> whiteSet;
    private Set<TagKey<?>> blackSet;
    private final String tagExpWhite;
    private final String tagExpBlack;
    // Cache isn't fast enough here, so I have to use map here.
    private final Reference2BooleanMap<Object> memory = new Reference2BooleanOpenHashMap<>();

    public TagPriorityList(Set<TagKey<?>> whiteKeys, Set<TagKey<?>> blackKeys, String tagExpWhite, String tagExpBlack) {
        this.whiteSet = whiteKeys;
        this.blackSet = blackKeys;
        this.tagExpWhite = tagExpWhite;
        this.tagExpBlack = tagExpBlack;
        INVALIDATOR.put(this, () -> {
            this.whiteSet = TagExpParser.getMatchingOre(this.tagExpWhite);
            this.blackSet = TagExpParser.getMatchingOre(this.tagExpBlack);
            this.memory.clear();
        });
    }

    public static void reset() {
        for (var e : INVALIDATOR.entrySet()) {
            e.getValue().run();
        }
    }

    @Override
    public boolean isListed(AEKey input) {
        Object key = input.getPrimaryKey();
        return this.memory.computeIfAbsent(key, this::eval);
    }

    @Override
    public boolean isEmpty() {
        return this.tagExpBlack.isEmpty() && this.tagExpWhite.isEmpty();
    }

    @Override
    public Iterable<AEKey> getItems() {
        return List.of();
    }

    private boolean eval(@NotNull Object obj) {
        Holder<?> refer = null;
        if (obj instanceof Item item) {
            refer = ForgeRegistries.ITEMS.getHolder(item).orElse(null);
        } else if (obj instanceof Fluid fluid) {
            refer = ForgeRegistries.FLUIDS.getHolder(fluid).orElse(null);
        }
        if (refer != null) {
            if (whiteSet.isEmpty()) {
                return false;
            }
            boolean pass = refer.tags().anyMatch(whiteSet::contains);
            if (pass) {
                if (!blackSet.isEmpty()) {
                    return refer.tags().noneMatch(blackSet::contains);
                }
                return true;
            }
        }
        return false;
    }

}
