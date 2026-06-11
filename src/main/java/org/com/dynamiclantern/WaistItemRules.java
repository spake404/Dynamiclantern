package org.com.dynamiclantern;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class WaistItemRules {
    public static final String BELT_SLOT = "belt";
    public static final List<String> DEFAULT_ITEM_IDS = List.of("minecraft:lantern", "minecraft:soul_lantern");

    private static volatile List<String> cachedIds = List.of();
    private static volatile Set<Item> cachedItems = Set.of();

    private WaistItemRules() {
    }

    public static boolean isBeltSlot(SlotContext slotContext) {
        return slotContext != null && BELT_SLOT.equals(slotContext.identifier());
    }

    public static boolean canEquipInBelt(ItemStack stack, SlotContext slotContext) {
        return isBeltSlot(slotContext) && isConfiguredWaistItem(stack);
    }

    public static boolean isConfiguredWaistItem(ItemStack stack) {
        return !stack.isEmpty() && configuredItems().contains(stack.getItem());
    }

    public static boolean isShaderLightItem(ItemStack stack) {
        return isConfiguredWaistItem(stack) && isLightEmittingBlockItem(stack);
    }

    public static boolean isBlockItem(ItemStack stack) {
        return blockByItem(stack) != Blocks.AIR;
    }

    public static Block blockByItem(ItemStack stack) {
        return stack.isEmpty() ? Blocks.AIR : Block.byItem(stack.getItem());
    }

    public static List<String> getConfiguredItemIds() {
        return normalizeIds(Config.WAIST_RENDERABLE_ITEMS.get());
    }

    public static Set<Item> getConfiguredItems() {
        return configuredItems();
    }

    public static Optional<Item> resolveItem(String rawId) {
        String id = normalizeId(rawId);
        if (id.isEmpty()) {
            return Optional.empty();
        }

        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location == null || !ForgeRegistries.ITEMS.containsKey(location)) {
            return Optional.empty();
        }

        Item item = ForgeRegistries.ITEMS.getValue(location);
        return item == null || item == Items.AIR ? Optional.empty() : Optional.of(item);
    }

    public static Optional<Item> addConfiguredItem(String rawId) {
        Optional<Item> item = resolveItem(rawId);
        if (item.isEmpty()) {
            return Optional.empty();
        }

        String id = itemId(item.get());
        List<String> ids = new ArrayList<>(getConfiguredItemIds());
        if (!ids.contains(id)) {
            ids.add(id);
            setConfiguredItemIds(ids);
        }
        return item;
    }

    public static boolean removeConfiguredItem(String rawId) {
        String id = normalizeId(rawId);
        List<String> ids = new ArrayList<>(getConfiguredItemIds());
        boolean removed = ids.remove(id);
        if (removed) {
            setConfiguredItemIds(ids);
        }
        return removed;
    }

    public static String itemId(Item item) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        return key == null ? "" : key.toString();
    }

    public static void invalidate() {
        cachedIds = List.of();
        cachedItems = Set.of();
    }

    private static boolean isLightEmittingBlockItem(ItemStack stack) {
        Block block = blockByItem(stack);
        return block != Blocks.AIR && block.defaultBlockState().getLightEmission() > 0;
    }

    private static void setConfiguredItemIds(Collection<String> ids) {
        Config.WAIST_RENDERABLE_ITEMS.set(normalizeIds(ids));
        invalidate();
        WaistItemCache.clearAll();
    }

    private static Set<Item> configuredItems() {
        List<String> ids = getConfiguredItemIds();
        if (!ids.equals(cachedIds)) {
            synchronized (WaistItemRules.class) {
                if (!ids.equals(cachedIds)) {
                    Set<Item> items = ConcurrentHashMap.newKeySet();
                    for (String id : ids) {
                        resolveItem(id).ifPresent(items::add);
                    }
                    cachedIds = ids;
                    cachedItems = Set.copyOf(items);
                }
            }
        }
        return cachedItems;
    }

    private static List<String> normalizeIds(Collection<? extends String> ids) {
        Set<String> seen = ConcurrentHashMap.newKeySet();
        List<String> normalized = new ArrayList<>();
        for (String id : ids) {
            String normalizedId = normalizeId(id);
            if (!normalizedId.isEmpty() && seen.add(normalizedId)) {
                normalized.add(normalizedId);
            }
        }
        return List.copyOf(normalized);
    }

    private static String normalizeId(String rawId) {
        return Objects.toString(rawId, "").trim();
    }
}
