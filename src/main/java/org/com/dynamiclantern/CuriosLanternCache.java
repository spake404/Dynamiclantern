package org.com.dynamiclantern;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CuriosLanternCache {
    private static final Map<UUID, ItemStack> LANTERNS = new ConcurrentHashMap<>();

    private CuriosLanternCache() {
    }

    public static ItemStack getOrRefresh(Player player) {
        ItemStack cached = LANTERNS.get(player.getUUID());
        if (cached != null) {
            return cached;
        }

        ItemStack found = scan(player);
        remember(player, found);
        return found;
    }

    public static void remember(Player player, ItemStack stack) {
        LANTERNS.put(player.getUUID(), Dynamiclantern.isLantern(stack) ? stack.copy() : ItemStack.EMPTY);
    }

    private static ItemStack scan(Player player) {
        Optional<SlotResult> result = CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(handler -> handler.findFirstCurio(Dynamiclantern::isLantern));
        return result.map(SlotResult::stack).orElse(ItemStack.EMPTY);
    }

    private static void refresh(Player player) {
        remember(player, scan(player));
    }

    @SubscribeEvent
    public static void onCurioChange(CurioChangeEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) {
            return;
        }

        ItemStack to = event.getTo();
        if (Dynamiclantern.isLantern(to)) {
            remember(player, to);
        } else if (Dynamiclantern.isLantern(event.getFrom())) {
            refresh(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        LANTERNS.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        LANTERNS.remove(event.getOriginal().getUUID());
        LANTERNS.remove(event.getEntity().getUUID());
    }
}
