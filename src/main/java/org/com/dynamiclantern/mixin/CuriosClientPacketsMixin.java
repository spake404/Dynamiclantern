package org.com.dynamiclantern.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.com.dynamiclantern.WaistItemCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.common.network.client.CuriosClientPackets;
import top.theillusivec4.curios.common.network.server.sync.SPacketSyncCurios;
import top.theillusivec4.curios.common.network.server.sync.SPacketSyncStack;

@Mixin(value = CuriosClientPackets.class, remap = false)
public abstract class CuriosClientPacketsMixin {
    @Inject(
            method = "handle(Ltop/theillusivec4/curios/common/network/server/sync/SPacketSyncStack;)V",
            at = @At("TAIL"))
    private static void dynamiclantern$clearWaistCacheAfterStackSync(
            SPacketSyncStack packet,
            CallbackInfo ci) {
        dynamiclantern$clearPlayerCache(packet.entityId());
    }

    @Inject(
            method = "handle(Ltop/theillusivec4/curios/common/network/server/sync/SPacketSyncCurios;)V",
            at = @At("TAIL"))
    private static void dynamiclantern$clearWaistCacheAfterFullSync(
            SPacketSyncCurios packet,
            CallbackInfo ci) {
        dynamiclantern$clearPlayerCache(packet.entityId);
    }

    private static void dynamiclantern$clearPlayerCache(int entityId) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            Entity entity = level.getEntity(entityId);
            if (entity instanceof Player player) {
                WaistItemCache.clear(player);
                return;
            }
        }

        WaistItemCache.clearAll();
    }
}
