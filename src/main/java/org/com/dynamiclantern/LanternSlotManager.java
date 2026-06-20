package org.com.dynamiclantern;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import top.theillusivec4.curios.api.CuriosApi;

public final class LanternSlotManager {
    private static final ResourceLocation LANTERN_SLOT_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(Dynamiclantern.MODID, "lantern_slot");

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
                        1.0D,
                        AttributeModifier.Operation.ADD_VALUE);
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
