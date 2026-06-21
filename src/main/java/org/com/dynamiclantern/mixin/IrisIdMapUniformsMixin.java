package org.com.dynamiclantern.mixin;

import net.irisshaders.iris.api.v0.item.IrisItemLightProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.com.dynamiclantern.Config;
import org.com.dynamiclantern.WaistItemCache;
import org.com.dynamiclantern.WaistItemRules;
import org.com.dynamiclantern.client.ShaderHeldLightMode;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.irisshaders.iris.uniforms.IdMapUniforms$HeldItemSupplier")
public class IrisIdMapUniformsMixin {
    @Shadow
    private InteractionHand hand;

    @Shadow
    private int lightValue;

    @Shadow
    private Vector3f lightColor;

    @Redirect(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack dynamiclantern$useCuriosLanternForOffhand(LocalPlayer player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (WaistItemRules.isShaderLightItem(heldItem)) {
            return WaistItemRules.shaderLightStack(heldItem);
        }

        if (hand == InteractionHand.OFF_HAND && Config.SHADER_OFFHAND_OVERRIDE.get()) {
            if (ShaderHeldLightMode.usesMainHandOnlyHeldLight()) {
                return heldItem;
            }
            ItemStack waistItem = WaistItemCache.getShaderLightOrRefresh(player);
            if (WaistItemRules.isShaderLightItem(waistItem)) {
                return WaistItemRules.shaderLightStack(waistItem);
            }
        }

        return heldItem;
    }

    @Inject(method = "update", at = @At("TAIL"))
    private void dynamiclantern$boostMainHandLightForLegacyShaders(CallbackInfo ci) {
        if (hand != InteractionHand.MAIN_HAND
                || !Config.SHADER_OFFHAND_OVERRIDE.get()
                || !ShaderHeldLightMode.usesMainHandOnlyHeldLight()) {
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        ItemStack waistItem = WaistItemCache.getShaderLightOrRefresh(player);
        if (!WaistItemRules.isShaderLightItem(waistItem)) {
            return;
        }

        ItemStack lightStack = WaistItemRules.shaderLightStack(waistItem);
        IrisItemLightProvider lightProvider = (IrisItemLightProvider) lightStack.getItem();
        int waistLightValue = lightProvider.getLightEmission(player, lightStack);
        if (waistLightValue > lightValue) {
            lightValue = waistLightValue;
            lightColor = lightProvider.getLightColor(player, lightStack);
        }
    }
}
