package org.com.dynamiclantern.mixin;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.com.dynamiclantern.WaistItemCache;
import org.com.dynamiclantern.WaistItemRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "dev.lambdaurora.lambdynlights.LambDynLights", remap = false)
public abstract class LambDynamicLightsMixin {
    @Shadow
    private static boolean isEyeSubmergedInFluid(LivingEntity entity) {
        throw new AssertionError();
    }

    @Shadow
    public static int getLuminanceFromItemStack(ItemStack stack, boolean submerged) {
        throw new AssertionError();
    }

    @Inject(method = "getLivingEntityLuminanceFromItems", at = @At("RETURN"), cancellable = true, require = 0)
    private static void dynamiclantern$includeVisibleWaistItems(
            LivingEntity entity,
            CallbackInfoReturnable<Integer> cir) {
        if (!(entity instanceof Player player)) {
            return;
        }

        boolean submerged = isEyeSubmergedInFluid(player);
        int luminance = cir.getReturnValue();
        for (WaistItemCache.CachedItem cachedItem : WaistItemCache.getVisibleWaistItemsOrRefresh(player)) {
            ItemStack stack = cachedItem.stack();
            int waistLuminance = getLuminanceFromItemStack(stack, submerged);
            if (waistLuminance == 0 && WaistItemRules.isShaderLightItem(stack)) {
                waistLuminance = getLuminanceFromItemStack(
                        WaistItemRules.shaderLightStack(stack),
                        submerged);
            }
            luminance = Math.max(luminance, waistLuminance);
        }

        cir.setReturnValue(Mth.clamp(luminance, 0, 15));
    }
}
