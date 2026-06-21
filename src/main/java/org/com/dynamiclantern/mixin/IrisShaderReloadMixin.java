package org.com.dynamiclantern.mixin;

import org.com.dynamiclantern.client.ShaderHeldLightMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.irisshaders.iris.Iris")
public abstract class IrisShaderReloadMixin {
    @Inject(method = "loadShaderpack", at = @At("TAIL"), require = 0)
    private static void dynamiclantern$refreshHeldLightModeAfterLoad(CallbackInfo ci) {
        ShaderHeldLightMode.refresh();
    }

    @Inject(method = "reload", at = @At("TAIL"), require = 0)
    private static void dynamiclantern$refreshHeldLightModeAfterReload(CallbackInfo ci) {
        ShaderHeldLightMode.refresh();
    }
}
