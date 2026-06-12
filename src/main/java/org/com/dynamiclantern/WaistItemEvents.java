package org.com.dynamiclantern;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import top.theillusivec4.curios.api.event.CurioChangeEvent;
import top.theillusivec4.curios.api.event.CurioCanEquipEvent;

public final class WaistItemEvents {
    private WaistItemEvents() {
    }

    @SubscribeEvent
    public static void onCurioEquip(CurioCanEquipEvent event) {
        if (WaistItemRules.canEquipInBelt(event.getStack(), event.getSlotContext())) {
            event.setEquipResult(TriState.TRUE);
        }
    }

    @SubscribeEvent
    public static void onCurioChange(CurioChangeEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player player && WaistItemRules.BELT_SLOT.equals(event.getIdentifier())) {
            WaistItemCache.refresh(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        WaistItemCache.clear(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        WaistItemCache.clear(event.getOriginal());
        WaistItemCache.clear(event.getEntity());
    }
}
