package org.com.dynamiclantern.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.com.dynamiclantern.Config;
import org.com.dynamiclantern.WaistItemCache;
import org.com.dynamiclantern.WaistItemRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "net.irisshaders.iris.uniforms.IdMapUniforms$HeldItemSupplier")
public class IrisIdMapUniformsMixin {
    @Redirect(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack dynamiclantern$useCuriosLanternForOffhand(LocalPlayer player, InteractionHand hand) {
        if (hand == InteractionHand.OFF_HAND && Config.SHADER_OFFHAND_OVERRIDE.get()) {
            ItemStack waistItem = WaistItemCache.getOrRefresh(player);
            if (WaistItemRules.isShaderLightItem(waistItem)) {
                return waistItem;
            }
        }

        return player.getItemInHand(hand);
    }
}
