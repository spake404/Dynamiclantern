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

    private WaistItemCache() {
    }

    public static ItemStack getOrRefresh(Player player) {
        ItemStack cached = WAIST_ITEMS.get(player.getUUID());
        if (cached != null && (cached.isEmpty() || WaistItemRules.isConfiguredWaistItem(cached))) {
            return cached;
        }

        return refresh(player);
    }

    public static void remember(Player player, ItemStack stack) {
        WAIST_ITEMS.put(player.getUUID(), WaistItemRules.isConfiguredWaistItem(stack) ? stack.copy() : ItemStack.EMPTY);
    }

    public static ItemStack refresh(Player player) {
        ItemStack found = scan(player);
        remember(player, found);
        return found;
    }

    public static void clear(Player player) {
        WAIST_ITEMS.remove(player.getUUID());
    }

    public static void clearAll() {
        WAIST_ITEMS.clear();
    }

    private static ItemStack scan(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(handler -> handler.findCurios(WaistItemRules.BELT_SLOT)
                        .stream()
                        .map(SlotResult::stack)
                        .filter(WaistItemRules::isConfiguredWaistItem)
                        .findFirst())
                .orElse(ItemStack.EMPTY);
    }
}
