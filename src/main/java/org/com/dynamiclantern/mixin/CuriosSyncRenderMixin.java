package org.com.dynamiclantern.mixin;

import org.com.dynamiclantern.Diagnostics;
import org.com.dynamiclantern.WaistItemCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.common.network.client.CuriosClientPayloadHandler;
import top.theillusivec4.curios.common.network.server.sync.SPacketSyncRender;

@Mixin(value = CuriosClientPayloadHandler.class, remap = false)
public abstract class CuriosSyncRenderMixin {
    @Inject(method = "lambda$handle$5", at = @At("TAIL"), require = 0)
    private static void dynamiclantern$clearWaistCacheAfterRenderSync(
            SPacketSyncRender packet,
            CallbackInfo ci) {
        Diagnostics.log(
                "curios-sync-render-" + packet.entityId() + "-" + packet.curioId() + "-" + packet.slotId(),
                "Curios render sync received entityId={}, slot={}#{}, visible={}, clearing Dynamic Lantern waist cache",
                packet.entityId(),
                packet.curioId(),
                packet.slotId(),
                packet.value());
        WaistItemCache.clearAll();
    }
}
