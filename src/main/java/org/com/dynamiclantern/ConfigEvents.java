package org.com.dynamiclantern;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(modid = Dynamiclantern.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
