package org.com.dynamiclantern.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.com.dynamiclantern.Config;
import org.com.dynamiclantern.WaistItemRules;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.client.render.CuriosLayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.compat.CuriosCompat;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class EpicFightCurioWaistItemRenderer extends CurioWaistItemRenderer implements CuriosCompat.EpicFightCurioRenderer {
    private static final float LEFT_SIDE_INSET_PIXELS = 2.0F / 16.0F;
    private static final SlotRule BELT_LANTERN_RULE = new SlotRule(
            "Hips",
            -0.05F,
            0.60F,
            0.0F,
            0.0F,
            0.0F,
            180.0F,
            0.90F,
            -0.05F,
            0.70F,
            -0.29F,
            0.20F,
            0.0F,
            180.0F);

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
        if (!Config.RENDER_WAIST_LANTERN.get()
                || !WaistItemRules.isBeltSlot(slotContext)
                || !WaistItemRules.isRenderableWaistItem(stack)
                || !(entity instanceof Player player)) {
            return;
        }

        poseStack.pushPose();

        applyRuleTransform(poseStack, entityPatch, poses, BELT_LANTERN_RULE, player);
        RenderLayerParent<LivingEntity, EntityModel<LivingEntity>> parent = getParentRenderer(player);
        if (parent != null) {
            float bodyYaw = Mth.rotLerp(partialTicks, player.yBodyRotO, player.yBodyRot);
            float headYaw = Mth.rotLerp(partialTicks, player.yHeadRotO, player.getYHeadRot());
            super.render(
                    stack,
                    slotContext,
                    poseStack,
                    parent,
                    buffers,
                    packedLight,
                    player.walkAnimation.position(partialTicks),
                    player.walkAnimation.speed(partialTicks),
                    partialTicks,
                    player.tickCount + partialTicks,
                    headYaw - bodyYaw,
                    Mth.lerp(partialTicks, player.xRotO, player.getXRot()));
        } else {
            WaistItemModelRenderer.render(stack, poseStack, buffers, packedLight);
        }

        poseStack.popPose();
    }

    private static void applyRuleTransform(
            PoseStack poseStack,
            LivingEntityPatch<LivingEntity> entityPatch,
            OpenMatrix4f[] poses,
            SlotRule rule,
            LivingEntity entity) {
        Armature armature = entityPatch.getArmature();
        JointLookup jointLookup = findJoint(armature, rule.bone);
        Joint joint = jointLookup.joint;
        if (joint != null) {
            MathUtils.mulStack(poseStack, poses[joint.getId()]);
        }
        if (jointLookup.applyHipFallbackFlip) {
            poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        }

        float offsetX = rule.offsetX(entity);
        if (Config.LEFT_SIDE.get()) {
            offsetX += LEFT_SIDE_INSET_PIXELS;
        }
        poseStack.translate(offsetX, rule.offsetY(entity), rule.offsetZ(entity));
        applyRotation(poseStack, Axis.XP, rule.rotationX(entity));
        applyRotation(poseStack, Axis.YP, rule.rotationY(entity));
        applyRotation(poseStack, Axis.ZP, rule.rotationZ(entity));

        float scale = rule.scale;
        if (!Float.isFinite(scale) || scale <= 0.0F) {
            scale = 1.0F;
        }
        if (scale != 1.0F) {
            poseStack.scale(scale, scale, scale);
        }
    }

    private static JointLookup findJoint(Armature armature, String bone) {
        Joint joint = armature.searchJointByName(bone);
        boolean hipFallback = false;
        if (joint == null && "Hips".equals(bone)) {
            joint = armature.searchJointByName("Pelvis");
            hipFallback = joint != null;
            if (joint == null) {
                joint = armature.searchJointByName("Hip");
                hipFallback = joint != null;
            }
            if (joint == null) {
                joint = armature.searchJointByName("Body");
                hipFallback = joint != null;
            }
        } else if (joint == null && "Chest".equals(bone)) {
            joint = armature.searchJointByName("Body");
            if (joint == null) {
                joint = armature.searchJointByName("Spine");
            }
        } else if (joint == null && "Head".equals(bone)) {
            joint = armature.searchJointByName("Neck");
        }

        if (joint == null) {
            joint = armature.searchJointByName("Root");
        }
        if (joint == null) {
            joint = armature.searchJointByName("Chest");
        }
        return new JointLookup(joint, hipFallback);
    }

    private static void applyRotation(PoseStack poseStack, Axis axis, float degrees) {
        if (degrees != 0.0F) {
            poseStack.mulPose(axis.rotationDegrees(degrees));
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static RenderLayerParent<LivingEntity, EntityModel<LivingEntity>> getParentRenderer(LivingEntity entity) {
        EntityRenderer<? extends Player> renderer;
        if (entity instanceof AbstractClientPlayer player) {
            renderer = Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap().get(player.getModelName());
        } else {
            renderer = Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap().get("default");
        }
        if (!(renderer instanceof LivingEntityRenderer<?, ?> livingRenderer)) {
            return null;
        }
        return (RenderLayerParent<LivingEntity, EntityModel<LivingEntity>>) (RenderLayerParent) livingRenderer;
    }

    private static boolean isSitting(LivingEntity entity) {
        return entity != null && (entity.isCrouching() || entity.isPassenger());
    }

    private static final class JointLookup {
        private final Joint joint;
        private final boolean applyHipFallbackFlip;

        private JointLookup(Joint joint, boolean applyHipFallbackFlip) {
            this.joint = joint;
            this.applyHipFallbackFlip = applyHipFallbackFlip;
        }
    }

    private static final class SlotRule {
        private final String bone;
        private final float offX;
        private final float offY;
        private final float offZ;
        private final float rotX;
        private final float rotY;
        private final float rotZ;
        private final float scale;
        private final float sitOffX;
        private final float sitOffY;
        private final float sitOffZ;
        private final float sitRotX;
        private final float sitRotY;
        private final float sitRotZ;

        private SlotRule(
                String bone,
                float offX,
                float offY,
                float offZ,
                float rotX,
                float rotY,
                float rotZ,
                float scale,
                float sitOffX,
                float sitOffY,
                float sitOffZ,
                float sitRotX,
                float sitRotY,
                float sitRotZ) {
            this.bone = bone;
            this.offX = offX;
            this.offY = offY;
            this.offZ = offZ;
            this.rotX = rotX;
            this.rotY = rotY;
            this.rotZ = rotZ;
            this.scale = scale;
            this.sitOffX = sitOffX;
            this.sitOffY = sitOffY;
            this.sitOffZ = sitOffZ;
            this.sitRotX = sitRotX;
            this.sitRotY = sitRotY;
            this.sitRotZ = sitRotZ;
        }

        private float offsetX(LivingEntity entity) {
            return isSitting(entity) && !Float.isNaN(sitOffX) ? sitOffX : offX;
        }

        private float offsetY(LivingEntity entity) {
            return isSitting(entity) && !Float.isNaN(sitOffY) ? sitOffY : offY;
        }

        private float offsetZ(LivingEntity entity) {
            return isSitting(entity) && !Float.isNaN(sitOffZ) ? sitOffZ : offZ;
        }

        private float rotationX(LivingEntity entity) {
            return isSitting(entity) && !Float.isNaN(sitRotX) ? sitRotX : rotX;
        }

        private float rotationY(LivingEntity entity) {
            return isSitting(entity) && !Float.isNaN(sitRotY) ? sitRotY : rotY;
        }

        private float rotationZ(LivingEntity entity) {
            return isSitting(entity) && !Float.isNaN(sitRotZ) ? sitRotZ : rotZ;
        }
    }
}
