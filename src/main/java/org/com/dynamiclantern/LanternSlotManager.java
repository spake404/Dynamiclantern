package org.com.dynamiclantern;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.UUID;

public final class LanternSlotManager {
    private static final UUID LANTERN_SLOT_MODIFIER_ID = UUID.fromString("72fef335-f6ec-4cf2-9c18-3347f3854a4d");
    private static final String LANTERN_SLOT_MODIFIER_NAME = "Dynamic Lantern lantern slot";

    private LanternSlotManager() {
    }

    public static void sync(Player player) {
        if (!(player instanceof ServerPlayer)) {
            return;
        }

        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            handler.removeSlotModifier(WaistItemRules.LANTERN_SLOT, LANTERN_SLOT_MODIFIER_ID);
            if (WaistItemRules.isLanternSlotEnabled()) {
                handler.addTransientSlotModifier(
                        WaistItemRules.LANTERN_SLOT,
                        LANTERN_SLOT_MODIFIER_ID,
                        LANTERN_SLOT_MODIFIER_NAME,
                        1.0D,
                        AttributeModifier.Operation.ADDITION);
            }
            handler.processSlots();
            handler.handleInvalidStacks();
        });
        WaistItemCache.clear(player);
    }

    public static void syncAll(MinecraftServer server) {
        if (server == null) {
            return;
        }

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sync(player);
        }
    }
}
