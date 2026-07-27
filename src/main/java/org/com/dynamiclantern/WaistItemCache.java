package org.com.dynamiclantern;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class WaistItemCache {
    private static final Map<UUID, CachedItem> WAIST_ITEMS = new ConcurrentHashMap<>();
    private static final Map<UUID, CachedItem> SHADER_LIGHT_ITEMS = new ConcurrentHashMap<>();
    private static final Map<UUID, List<CachedItem>> VISIBLE_WAIST_ITEMS = new ConcurrentHashMap<>();
    private static final List<WaistItemSource> OPTIONAL_SOURCES = new CopyOnWriteArrayList<>();
    private static final Comparator<CachedItem> ITEM_PRIORITY = Comparator
            .comparingInt((CachedItem item) -> WaistItemRules.slotPriority(item.slot().identifier()))
            .thenComparingInt(item -> item.slot().source().priority())
            .thenComparingInt(item -> item.slot().index());

    private WaistItemCache() {
    }

    public static void registerSource(WaistItemSource source) {
        if (!OPTIONAL_SOURCES.contains(source)) {
            OPTIONAL_SOURCES.add(source);
            clearAll();
        }
    }

    public static ItemStack getOrRefresh(Player player) {
        return getVisibleWaistItemOrRefresh(player).stack();
    }

    public static CachedItem getVisibleWaistItemOrRefresh(Player player) {
        CachedItem cached = WAIST_ITEMS.get(player.getUUID());
        if (cached != null && isRenderableCachedItem(cached)) {
            return cached;
        }
        return refreshAll(player).waistItem();
    }

    public static ItemStack getShaderLightOrRefresh(Player player) {
        CachedItem cached = SHADER_LIGHT_ITEMS.get(player.getUUID());
        if (cached != null && isShaderLightCachedItem(cached)) {
            return cached.stack();
        }
        return refreshAll(player).shaderLightItem().stack();
    }

    public static List<CachedItem> getVisibleWaistItemsOrRefresh(Player player) {
        List<CachedItem> cached = VISIBLE_WAIST_ITEMS.get(player.getUUID());
        if (cached != null && cached.stream().allMatch(WaistItemCache::isRenderableCachedItem)) {
            return cached;
        }
        return refreshAll(player).visibleWaistItems();
    }

    public static void remember(Player player, SlotContext slotContext, ItemStack stack) {
        remember(player, WaistSlot.fromCurios(slotContext), stack);
    }

    public static void remember(Player player, WaistSlot slot, ItemStack stack) {
        CachedItem candidate = slot != null
                && WaistItemRules.isVisibleWaistItemSlot(slot)
                && WaistItemRules.isRenderableWaistItem(stack)
                ? new CachedItem(stack, slot)
                : CachedItem.empty();
        CachedItem cached = WAIST_ITEMS.get(player.getUUID());
        if (!candidate.isEmpty() && cached != null && ITEM_PRIORITY.compare(cached, candidate) < 0) {
            return;
        }
        remember(WAIST_ITEMS, player.getUUID(), candidate);
    }

    public static ItemStack refresh(Player player) {
        return refreshAll(player).waistItem().stack();
    }

    public static ItemStack refreshShaderLight(Player player) {
        return refreshAll(player).shaderLightItem().stack();
    }

    public static void clear(Player player) {
        UUID playerId = player.getUUID();
        WAIST_ITEMS.remove(playerId);
        SHADER_LIGHT_ITEMS.remove(playerId);
        VISIBLE_WAIST_ITEMS.remove(playerId);
    }

    public static void clearAll() {
        WAIST_ITEMS.clear();
        SHADER_LIGHT_ITEMS.clear();
        VISIBLE_WAIST_ITEMS.clear();
    }

    private static CacheResult refreshAll(Player player) {
        List<CachedItem> visibleItems = new ArrayList<>();
        collectCuriosItems(player, visibleItems);
        for (WaistItemSource source : OPTIONAL_SOURCES) {
            for (CachedItem item : source.findVisibleWaistItems(player)) {
                if (isRenderableCachedItem(item)) {
                    visibleItems.add(item.copy());
                }
            }
        }
        visibleItems.sort(ITEM_PRIORITY);

        CachedItem waistItem = visibleItems.isEmpty() ? CachedItem.empty() : visibleItems.getFirst();
        CachedItem shaderLightItem = visibleItems.stream()
                .filter(item -> WaistItemRules.isShaderLightItem(item.stack()))
                .findFirst()
                .orElseGet(CachedItem::empty);
        List<CachedItem> immutableItems = visibleItems.stream().map(CachedItem::copy).toList();

        UUID playerId = player.getUUID();
        remember(WAIST_ITEMS, playerId, waistItem);
        remember(SHADER_LIGHT_ITEMS, playerId, shaderLightItem);
        VISIBLE_WAIST_ITEMS.put(playerId, immutableItems);
        if (!waistItem.isEmpty() || !shaderLightItem.isEmpty()) {
            Diagnostics.log(
                    "cache-result-" + playerId,
                    "cache result player={}, waistItem={}, waistSlot={}, shaderItem={}, shaderSlot={}, candidates={}",
                    Diagnostics.playerName(player),
                    Diagnostics.itemId(waistItem.stack()),
                    Diagnostics.slot(waistItem.slot()),
                    Diagnostics.itemId(shaderLightItem.stack()),
                    Diagnostics.slot(shaderLightItem.slot()),
                    immutableItems.size());
        }
        return new CacheResult(waistItem, shaderLightItem, immutableItems);
    }

    private static void collectCuriosItems(Player player, List<CachedItem> items) {
        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            for (String slotId : WaistItemRules.renderSlotIdsByPriority()) {
                for (SlotResult result : handler.findCurios(slotId)) {
                    WaistSlot slot = WaistSlot.fromCurios(result.slotContext());
                    ItemStack stack = result.stack();
                    if (Diagnostics.isInteresting(stack)) {
                        Diagnostics.log(
                                "cache-scan-curios-" + player.getUUID(),
                                "cache scan source=curios player={}, item={}, slot={}, renderable={}, shaderLight={}",
                                Diagnostics.playerName(player),
                                Diagnostics.itemId(stack),
                                Diagnostics.slot(slot),
                                WaistItemRules.isRenderableWaistItem(stack),
                                WaistItemRules.isShaderLightItem(stack));
                    }
                    if (WaistItemRules.isVisibleWaistItemSlot(slot)
                            && WaistItemRules.isRenderableWaistItem(stack)) {
                        items.add(new CachedItem(stack, slot));
                    }
                }
            }
        });
    }

    private static void remember(Map<UUID, CachedItem> cache, UUID playerId, CachedItem item) {
        CachedItem value = item.copy();
        CachedItem cached = cache.get(playerId);
        if (cached != null && cached.matches(value)) {
            return;
        }
        cache.put(playerId, value);
    }

    private static boolean isRenderableCachedItem(CachedItem cached) {
        return cached.isEmpty()
                || (WaistItemRules.isVisibleWaistItemSlot(cached.slot())
                && WaistItemRules.isRenderableWaistItem(cached.stack()));
    }

    private static boolean isShaderLightCachedItem(CachedItem cached) {
        return cached.isEmpty()
                || (WaistItemRules.isVisibleWaistItemSlot(cached.slot())
                && WaistItemRules.isShaderLightItem(cached.stack()));
    }

    private record CacheResult(
            CachedItem waistItem,
            CachedItem shaderLightItem,
            List<CachedItem> visibleWaistItems) {
    }

    @FunctionalInterface
    public interface WaistItemSource {
        List<CachedItem> findVisibleWaistItems(Player player);
    }

    public record CachedItem(ItemStack stack, WaistSlot slot) {
        private static CachedItem empty() {
            return new CachedItem(ItemStack.EMPTY, null);
        }

        public boolean isEmpty() {
            return stack.isEmpty();
        }

        private CachedItem copy() {
            return isEmpty() ? empty() : new CachedItem(stack.copy(), slot);
        }

        private boolean matches(CachedItem other) {
            return ItemStack.matches(stack, other.stack) && sameSlot(slot, other.slot);
        }

        public boolean matches(ItemStack otherStack, WaistSlot otherSlot) {
            return ItemStack.matches(stack, otherStack) && sameSlot(slot, otherSlot);
        }

        public boolean matches(ItemStack otherStack, SlotContext otherSlotContext) {
            return ItemStack.matches(stack, otherStack)
                    && slot != null
                    && slot.matches(otherSlotContext);
        }

        private static boolean sameSlot(WaistSlot first, WaistSlot second) {
            return first == null ? second == null : first.equals(second);
        }
    }
}
