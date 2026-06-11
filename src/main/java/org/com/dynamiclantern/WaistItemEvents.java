package org.com.dynamiclantern;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.event.CurioChangeEvent;
import top.theillusivec4.curios.api.event.CurioEquipEvent;

public final class WaistItemEvents {
    private WaistItemEvents() {
    }

    @SubscribeEvent
    public static void onCurioEquip(CurioEquipEvent event) {
        if (WaistItemRules.canEquipInBelt(event.getStack(), event.getSlotContext())) {
            event.setResult(Event.Result.ALLOW);
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
