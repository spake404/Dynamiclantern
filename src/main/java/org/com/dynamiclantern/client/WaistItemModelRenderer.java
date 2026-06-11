package org.com.dynamiclantern.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.com.dynamiclantern.WaistItemRules;

final class WaistItemModelRenderer {
    private WaistItemModelRenderer() {
    }

    static void render(ItemStack stack, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        Minecraft minecraft = Minecraft.getInstance();
        BakedModel model = minecraft.getItemRenderer().getModel(stack, minecraft.level, null, 0);
        if (WaistItemRules.isBlockItem(stack) && !model.isCustomRenderer()) {
            Block block = WaistItemRules.blockByItem(stack);
            minecraft.getBlockRenderer().renderSingleBlock(
                    block.defaultBlockState(),
                    poseStack,
                    buffers,
                    packedLight,
                    OverlayTexture.NO_OVERLAY);
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.scale(0.75F, 0.75F, 0.75F);
        minecraft.getItemRenderer().renderStatic(
                stack,
                ItemDisplayContext.FIXED,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffers,
                minecraft.level,
                0);
        poseStack.popPose();
    }
}
