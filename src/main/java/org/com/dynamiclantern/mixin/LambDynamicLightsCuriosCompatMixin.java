package org.com.dynamiclantern.mixin;

import org.com.dynamiclantern.WaistItemRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.Map;

@Pseudo
@Mixin(targets = "dev.lambdaurora.lambdynlights.compat.CuriosCompat", remap = false)
public abstract class LambDynamicLightsCuriosCompatMixin {
    @Redirect(
            method = "getLivingEntityLuminanceFromItems",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;"),
            require = 0)
    private Collection<?> dynamiclantern$excludeManagedWaistSlots(Map<?, ?> curios) {
        return curios.entrySet().stream()
                .filter(entry -> !(entry.getKey() instanceof String identifier)
                        || !WaistItemRules.isKnownWaistItemSlot(identifier))
                .map(Map.Entry::getValue)
                .toList();
    }
}
