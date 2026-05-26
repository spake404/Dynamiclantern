package org.com.dynamiclantern.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.com.dynamiclantern.Config;
import org.com.dynamiclantern.CuriosLanternCache;
import org.joml.Quaternionf;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.client.render.CuriosLayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.compat.curiosapi.CuriosCompat;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class EpicFightCurioLanternRenderer extends CurioLanternRenderer implements CuriosCompat.EpicFightCurioRenderer {
    @Override
    public void draw(
            ItemStack stack,
            SlotContext slotContext,
            LivingEntityPatch<LivingEntity> entityPatch,
            LivingEntity entity,
            CuriosLayer<LivingEntity, net.minecraft.client.model.EntityModel<LivingEntity>> vanillaLayer,
            PoseStack poseStack,
            MultiBufferSource buffers,
            int packedLight,
            OpenMatrix4f[] poses,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float partialTicks) {
        if (!Config.RENDER_WAIST_LANTERN.get() || !(entity instanceof Player player)) {
            return;
        }

        CuriosLanternCache.remember(player, stack);
        poseStack.pushPose();

        applyHipTransform(poseStack, entityPatch, poses, player);
        Vec3 swing = LanternPhysics.update(player, player.getPosition(partialTicks), partialTicks);
        poseStack.mulPose(new Quaternionf().rotationZYX((float) swing.x, 0.0F, (float) swing.z));
        renderLanternBlock(stack, poseStack, buffers, packedLight);

        poseStack.popPose();
    }

    private static void applyHipTransform(PoseStack poseStack, LivingEntityPatch<LivingEntity> entityPatch, OpenMatrix4f[] poses, Player player) {
        Joint joint = findHipJoint(entityPatch.getArmature());
        if (joint != null && joint.getId() >= 0 && joint.getId() < poses.length) {
            MathUtils.mulStack(poseStack, poses[joint.getId()]);
        }

        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

        float side = Config.LEFT_SIDE.get() ? 0.05F : -0.05F;
        if (player.isCrouching()) {
            poseStack.translate(side, 0.70F, -0.29F);
            poseStack.mulPose(Axis.XP.rotationDegrees(0.2F));
        } else {
            poseStack.translate(side, 0.60F, 0.0F);
        }
        poseStack.scale(0.90F, 0.90F, 0.90F);
    }

    private static Joint findHipJoint(Armature armature) {
        Joint joint = armature.searchJointByName("Hips");
        if (joint == null) {
            joint = armature.searchJointByName("Pelvis");
        }
        if (joint == null) {
            joint = armature.searchJointByName("Hip");
        }
        if (joint == null) {
            joint = armature.searchJointByName("Body");
        }
        if (joint == null) {
            joint = armature.searchJointByName("Root");
        }
        return joint;
    }

    private static void renderLanternBlock(ItemStack stack, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        poseStack.translate(-0.30F, -0.68F, -0.30F);
        poseStack.scale(0.60F, 0.60F, 0.60F);
        Block block = Block.byItem(stack.getItem());
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                block.defaultBlockState(),
                poseStack,
                buffers,
                packedLight,
                OverlayTexture.NO_OVERLAY);
    }
}
