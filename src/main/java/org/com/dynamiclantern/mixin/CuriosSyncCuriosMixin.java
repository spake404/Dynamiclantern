package org.com.dynamiclantern.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.com.dynamiclantern.WaistItemCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.common.network.server.sync.SPacketSyncCurios;

@Mixin(value = SPacketSyncCurios.class, remap = false)
public abstract class CuriosSyncCuriosMixin {
    @Inject(method = "lambda$handle$0", at = @At("TAIL"), require = 0)
    private static void dynamiclantern$clearWaistCacheAfterFullSync(
            SPacketSyncCurios packet,
            Entity entity,
            ICuriosItemHandler curiosHandler,
            CallbackInfo ci) {
        if (entity instanceof Player player) {
            WaistItemCache.clear(player);
        }
    }
}
