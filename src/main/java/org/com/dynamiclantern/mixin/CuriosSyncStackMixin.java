package org.com.dynamiclantern.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.com.dynamiclantern.WaistItemCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.common.network.server.sync.SPacketSyncStack;

@Mixin(value = SPacketSyncStack.class, remap = false)
public abstract class CuriosSyncStackMixin {
    @Inject(method = "lambda$handle$1", at = @At("TAIL"), require = 0)
    private static void dynamiclantern$clearWaistCacheAfterStackSync(
            SPacketSyncStack packet,
            Entity entity,
            ICurioStacksHandler stacksHandler,
            CallbackInfo ci) {
        if (entity instanceof Player player) {
            WaistItemCache.clear(player);
        }
    }
}
