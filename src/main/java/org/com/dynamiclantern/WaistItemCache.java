package org.com.dynamiclantern;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class WaistItemCache {
    private static final Map<UUID, CachedItem> WAIST_ITEMS = new ConcurrentHashMap<>();
    private static final Map<UUID, CachedItem> SHADER_LIGHT_ITEMS = new ConcurrentHashMap<>();

    private WaistItemCache() {
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

        return refreshShaderLight(player);
    }

    public static void remember(Player player, SlotContext slotContext, ItemStack stack) {
        remember(
                WAIST_ITEMS,
                player.getUUID(),
                WaistItemRules.isVisibleBeltSlot(slotContext) && WaistItemRules.isRenderableWaistItem(stack)
                        ? new CachedItem(stack, slotContext)
                        : CachedItem.empty());
    }

    public static ItemStack refresh(Player player) {
        return refreshAll(player).waistItem().stack();
    }

    public static ItemStack refreshShaderLight(Player player) {
        return refreshAll(player).shaderLightItem().stack();
    }

    public static void clear(Player player) {
        WAIST_ITEMS.remove(player.getUUID());
        SHADER_LIGHT_ITEMS.remove(player.getUUID());
    }

    public static void clearAll() {
        WAIST_ITEMS.clear();
        SHADER_LIGHT_ITEMS.clear();
    }

    private static CacheResult refreshAll(Player player) {
        CachedItem waistItem = CachedItem.empty();
        CachedItem shaderLightItem = CachedItem.empty();

        var optionalHandler = CuriosApi.getCuriosInventory(player);
        if (optionalHandler.isPresent()) {
            for (SlotResult result : optionalHandler.get().findCurios(WaistItemRules.BELT_SLOT)) {
                if (!WaistItemRules.isVisibleBeltSlot(result.slotContext())) {
                    continue;
                }

                ItemStack stack = result.stack();
                if (waistItem.isEmpty() && WaistItemRules.isRenderableWaistItem(stack)) {
                    waistItem = new CachedItem(stack, result.slotContext());
                }
                if (shaderLightItem.isEmpty() && WaistItemRules.isShaderLightItem(stack)) {
                    shaderLightItem = new CachedItem(stack, result.slotContext());
                }
                if (!waistItem.isEmpty() && !shaderLightItem.isEmpty()) {
                    break;
                }
            }
        }

        UUID playerId = player.getUUID();
        remember(WAIST_ITEMS, playerId, waistItem);
        remember(SHADER_LIGHT_ITEMS, playerId, shaderLightItem);
        return new CacheResult(waistItem, shaderLightItem);
    }

    private static void remember(Map<UUID, CachedItem> cache, UUID playerId, CachedItem item) {
        CachedItem value = item.isEmpty() ? CachedItem.empty() : item.copy();
        CachedItem cached = cache.get(playerId);
        if (cached != null && cached.matches(value)) {
            return;
        }

        cache.put(playerId, value);
    }

    private static boolean isRenderableCachedItem(CachedItem cached) {
        return cached.isEmpty()
                || (WaistItemRules.isVisibleBeltSlot(cached.slotContext())
                && WaistItemRules.isRenderableWaistItem(cached.stack()));
    }

    private static boolean isShaderLightCachedItem(CachedItem cached) {
        return cached.isEmpty()
                || (WaistItemRules.isVisibleBeltSlot(cached.slotContext())
                && WaistItemRules.isShaderLightItem(cached.stack()));
    }

    private record CacheResult(CachedItem waistItem, CachedItem shaderLightItem) {
    }

    public record CachedItem(ItemStack stack, SlotContext slotContext) {
        private static CachedItem empty() {
            return new CachedItem(ItemStack.EMPTY, null);
        }

        public boolean isEmpty() {
            return stack.isEmpty();
        }

        private CachedItem copy() {
            return isEmpty() ? empty() : new CachedItem(stack.copy(), slotContext);
        }

        private boolean matches(CachedItem other) {
            return ItemStack.matches(stack, other.stack)
                    && sameSlot(slotContext, other.slotContext);
        }

        private static boolean sameSlot(SlotContext first, SlotContext second) {
            if (first == null || second == null) {
                return first == second;
            }
            return first.identifier().equals(second.identifier())
                    && first.index() == second.index()
                    && first.cosmetic() == second.cosmetic()
                    && first.visible() == second.visible();
        }
    }
}
