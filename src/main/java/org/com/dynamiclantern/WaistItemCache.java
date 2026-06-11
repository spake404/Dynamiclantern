package org.com.dynamiclantern;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

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
        WAIST_ITEMS.put(player.getUUID(), WaistItemRules.isRenderableWaistItem(stack) ? stack.copy() : ItemStack.EMPTY);
    }

    public static ItemStack refresh(Player player) {
        ItemStack found = scan(player, WaistItemRules::isRenderableWaistItem);
        remember(player, found);
        refreshShaderLight(player);
        return found;
    }

    public static ItemStack refreshShaderLight(Player player) {
        ItemStack found = scan(player, WaistItemRules::isShaderLightItem);
        SHADER_LIGHT_ITEMS.put(player.getUUID(), found.isEmpty() ? ItemStack.EMPTY : found.copy());
        return found;
    }

    public static void clear(Player player) {
        WAIST_ITEMS.remove(player.getUUID());
        SHADER_LIGHT_ITEMS.remove(player.getUUID());
    }

    public static void clearAll() {
        WAIST_ITEMS.clear();
        SHADER_LIGHT_ITEMS.clear();
    }

    private static ItemStack scan(Player player, Predicate<ItemStack> predicate) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(handler -> handler.findCurios(WaistItemRules.BELT_SLOT)
                        .stream()
                        .map(SlotResult::stack)
                        .filter(predicate)
                        .findFirst())
                .orElse(ItemStack.EMPTY);
    }
}
