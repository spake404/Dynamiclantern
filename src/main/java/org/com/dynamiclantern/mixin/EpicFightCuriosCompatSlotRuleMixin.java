package org.com.dynamiclantern.mixin;

import net.minecraft.world.item.ItemStack;
import org.com.dynamiclantern.Diagnostics;
import org.com.dynamiclantern.WaistItemRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.oneworldstudio.epicfightcurioscompat.ClientCuriosCompat$SlotRule", remap = false)
public abstract class EpicFightCuriosCompatSlotRuleMixin {
    @Inject(method = "isLanternLike", at = @At("HEAD"), cancellable = true, require = 0)
    private static void dynamiclantern$useDynamicLanternRenderer(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (WaistItemRules.isRenderableWaistItem(stack)) {
            Diagnostics.log(
                    "efcc-is-lantern-like-" + Diagnostics.itemId(stack),
                    "EFCC SlotRule.isLanternLike intercepted item={}, return=false",
                    Diagnostics.itemId(stack));
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isSupportedFallback", at = @At("HEAD"), cancellable = true, require = 0)
    private static void dynamiclantern$skipDynamicLanternFallback(String slotId, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (isDynamicLanternWaistItem(slotId, stack)) {
            Diagnostics.log(
                    "efcc-supported-fallback-" + Diagnostics.itemId(stack),
                    "EFCC SlotRule.isSupportedFallback intercepted item={}, slotId={}, return=false",
                    Diagnostics.itemId(stack),
                    slotId);
            cir.setReturnValue(false);
        }
    }

    private static boolean isDynamicLanternWaistItem(String slotId, ItemStack stack) {
        return ("belt".equals(slotId) || "waist".equals(slotId))
                && WaistItemRules.isRenderableWaistItem(stack);
    }
}
