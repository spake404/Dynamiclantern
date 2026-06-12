package org.com.dynamiclantern.mixin;

import net.minecraft.world.item.ItemStack;
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
            cir.setReturnValue(false);
        }
    }
}
