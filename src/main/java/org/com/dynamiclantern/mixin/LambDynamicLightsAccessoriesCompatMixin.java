package org.com.dynamiclantern.mixin;

import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import org.com.dynamiclantern.WaistItemRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Pseudo
@Mixin(targets = "dev.lambdaurora.lambdynlights.compat.AccessoriesCompat", remap = false)
public abstract class LambDynamicLightsAccessoriesCompatMixin {
    @Redirect(
            method = "getLivingEntityLuminanceFromItems",
            at = @At(
                    value = "INVOKE",
                    target = "Lio/wispforest/accessories/api/AccessoriesCapability;getAllEquipped()Ljava/util/List;"),
            require = 0)
    private List<SlotEntryReference> dynamiclantern$excludeManagedWaistSlots(AccessoriesCapability capability) {
        return capability.getAllEquipped().stream()
                .filter(entry -> !WaistItemRules.isKnownWaistItemSlot(entry.reference().slotName()))
                .toList();
    }
}
