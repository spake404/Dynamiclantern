package org.com.dynamiclantern.mixin;

import org.com.dynamiclantern.WaistItemCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.common.network.server.sync.SPacketSyncRender;

@Mixin(value = SPacketSyncRender.class, remap = false)
public abstract class CuriosSyncRenderMixin {
    @Inject(method = "lambda$handle$0", at = @At("TAIL"), require = 0)
    private static void dynamiclantern$clearWaistCacheAfterRenderSync(
            SPacketSyncRender packet,
            ICurioStacksHandler stacksHandler,
            CallbackInfo ci) {
        WaistItemCache.clearAll();
    }
}
