package org.com.dynamiclantern;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

@EventBusSubscriber(modid = Dynamiclantern.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ConfigEvents {
    private ConfigEvents() {
    }

    @SubscribeEvent
    public static void onConfigLoading(ModConfigEvent.Loading event) {
        syncLanternSlotConfig(event);
    }

    @SubscribeEvent
    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        syncLanternSlotConfig(event);
    }

    private static void syncLanternSlotConfig(ModConfigEvent event) {
        ModConfig config = event.getConfig();
        if (config.getType() != ModConfig.Type.COMMON || !Dynamiclantern.MODID.equals(config.getModId())) {
            return;
        }

        WaistItemCache.clearAll();
        LanternSlotManager.syncAll(ServerLifecycleHooks.getCurrentServer());
    }
}
