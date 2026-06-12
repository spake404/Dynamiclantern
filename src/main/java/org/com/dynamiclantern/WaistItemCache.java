package org.com.dynamiclantern;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class WaistItemCache {
    private static final Map<UUID, ItemStack> WAIST_ITEMS = new ConcurrentHashMap<>();
    private static final Map<UUID, ItemStack> SHADER_LIGHT_ITEMS = new ConcurrentHashMap<>();

    private WaistItemCache() {
    }

    public static ItemStack getOrRefresh(Player player) {
        ItemStack cached = WAIST_ITEMS.get(player.getUUID());
        if (cached != null && (cached.isEmpty() || WaistItemRules.isRenderableWaistItem(cached))) {
            return cached;
        }

        return refresh(player);
    }

    public static ItemStack getShaderLightOrRefresh(Player player) {
        ItemStack cached = SHADER_LIGHT_ITEMS.get(player.getUUID());
        if (cached != null && (cached.isEmpty() || WaistItemRules.isShaderLightItem(cached))) {
            return cached;
        }

        return refreshShaderLight(player);
    }

    public static void remember(Player player, ItemStack stack) {
        remember(WAIST_ITEMS, player.getUUID(), WaistItemRules.isRenderableWaistItem(stack) ? stack : ItemStack.EMPTY);
    }

    public static ItemStack refresh(Player player) {
        return refreshAll(player).waistItem();
    }

    public static ItemStack refreshShaderLight(Player player) {
        return refreshAll(player).shaderLightItem();
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
        ItemStack waistItem = ItemStack.EMPTY;
        ItemStack shaderLightItem = ItemStack.EMPTY;

        var optionalHandler = CuriosApi.getCuriosInventory(player).resolve();
        if (optionalHandler.isPresent()) {
            for (SlotResult result : optionalHandler.get().findCurios(WaistItemRules.BELT_SLOT)) {
                ItemStack stack = result.stack();
                if (waistItem.isEmpty() && WaistItemRules.isRenderableWaistItem(stack)) {
                    waistItem = stack;
                }
                if (shaderLightItem.isEmpty() && WaistItemRules.isShaderLightItem(stack)) {
                    shaderLightItem = stack;
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

    private static void remember(Map<UUID, ItemStack> cache, UUID playerId, ItemStack stack) {
        ItemStack value = stack.isEmpty() ? ItemStack.EMPTY : stack;
        ItemStack cached = cache.get(playerId);
        if (cached != null && ItemStack.matches(cached, value)) {
            return;
        }

        cache.put(playerId, value.isEmpty() ? ItemStack.EMPTY : value.copy());
    }

    private record CacheResult(ItemStack waistItem, ItemStack shaderLightItem) {
    }
}
