package org.com.dynamiclantern.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.com.dynamiclantern.Config;
import org.com.dynamiclantern.WaistItemCache;
import org.com.dynamiclantern.WaistItemRules;
import org.com.dynamiclantern.mixin.ModelPartAccessor;
import org.joml.Quaternionf;
import org.joml.Vector4f;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.util.ArrayList;
import java.util.List;

public class CurioWaistItemRenderer implements ICurioRenderer {
    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            ItemStack stack,
            SlotContext slotContext,
            PoseStack poseStack,
            RenderLayerParent<T, M> parent,
            MultiBufferSource buffers,
            int packedLight,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch) {
        if (!Config.RENDER_WAIST_LANTERN.get()
                || !WaistItemRules.isBeltSlot(slotContext)
                || !WaistItemRules.isRenderableWaistItem(stack)
                || !(slotContext.entity() instanceof Player player)
                || !(parent.getModel() instanceof PlayerModel<?> playerModel)) {
            return;
        }

        WaistItemCache.remember(player, stack);
        poseStack.pushPose();

        boolean armored = hasTorsoOrLegArmor(player);
        Vec3 bodyAnchor = getWaistAnchor(armored);
        attachToBody(poseStack, playerModel.body, bodyAnchor);

        float pivotY = swingPivotY(stack);
        poseStack.translate(0.5F, pivotY, 0.5F);

        Vector4f localPosition = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
        localPosition.mulTranspose(poseStack.last().pose());
        Vec3 worldAnchor = new Vec3(localPosition.x(), localPosition.y(), localPosition.z()).add(player.getPosition(partialTicks));

        Vec3 swing = LanternPhysics.update(player, worldAnchor, partialTicks);
        float legOffset = Math.min(0.0F, playerModel.rightLeg.xRot / 3.0F);
        float pitch = (float) swing.z + legOffset - (Config.BACK_LANTERN.get() ? -0.1F : 0.1F) - playerModel.body.xRot;

        poseStack.mulPose(new Quaternionf().rotationZYX((float) swing.x, 0.0F, pitch));
        poseStack.translate(-0.5F, -pivotY, -0.5F);
        applyModelOffset(stack, poseStack);

        WaistItemModelRenderer.render(stack, poseStack, buffers, packedLight);

        poseStack.popPose();
    }

    private static Vec3 getWaistAnchor(boolean armored) {
        float sideAnchor = Config.LEFT_SIDE.get()
                ? (armored ? -0.05F : 0.1F)
                : (armored ? 2.05F : 1.9F);
        float back = Config.BACK_LANTERN.get() ? (armored ? -3.1F : -3.0F) : -1.0F;
        return armored
                ? new Vec3(sideAnchor, -1.25F, back + 0.05F)
                : new Vec3(sideAnchor, -1.25F, back - 0.1F);
    }

    private static float swingPivotY(ItemStack stack) {
        return WaistItemRules.isColdSweatSoulspringLamp(stack) ? 1.18F : 11.0F / 16.0F;
    }

    private static void applyModelOffset(ItemStack stack, PoseStack poseStack) {
        if (WaistItemRules.isColdSweatSoulspringLamp(stack)) {
            poseStack.translate(0.0F, -0.28F, 0.0F);
        }
    }

    private static void attachToBody(PoseStack poseStack, ModelPart body, Vec3 anchor) {
        body.translateAndRotate(poseStack);
        Pair<Vec3, Vec3> bounds = measure(body);
        poseStack.scale(1.0F / 16.0F, 1.0F / 16.0F, 1.0F / 16.0F);
        poseStack.translate(
                Mth.lerp((-anchor.x + 1.0D) / 2.0D, bounds.getFirst().x, bounds.getSecond().x),
                Mth.lerp((-anchor.y + 1.0D) / 2.0D, bounds.getFirst().y, bounds.getSecond().y),
                Mth.lerp((-anchor.z + 1.0D) / 2.0D, bounds.getFirst().z, bounds.getSecond().z));
        poseStack.scale(8.0F, 8.0F, 8.0F);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
    }

    private static Pair<Vec3, Vec3> measure(ModelPart body) {
        Vec3 min = Vec3.ZERO;
        Vec3 max = Vec3.ZERO;
        List<ModelPart> parts = new ArrayList<>();
        parts.add(body);

        if (body.getClass().getSimpleName().contains("EMFModelPart")) {
            parts.addAll(((ModelPartAccessor) (Object) body).dynamiclantern$getChildren().values());
        }

        for (ModelPart part : parts) {
            for (ModelPart.Cube cube : ((ModelPartAccessor) (Object) part).dynamiclantern$getCubes()) {
                min = new Vec3(
                        Math.min(min.x, Math.min(cube.minX + part.x, cube.maxX + part.x)),
                        Math.min(min.y, Math.min(cube.minY + part.y, cube.maxY + part.y)),
                        Math.min(min.z, Math.min(cube.minZ + part.z, cube.maxZ + part.z)));
                max = new Vec3(
                        Math.max(max.x, Math.max(cube.minX + part.x, cube.maxX + part.x)),
                        Math.max(max.y, Math.max(cube.minY + part.y, cube.maxY + part.y)),
                        Math.max(max.z, Math.max(cube.minZ + part.z, cube.maxZ + part.z)));
            }
        }

        return Pair.of(min, max);
    }

    private static boolean hasTorsoOrLegArmor(Player player) {
        for (ItemStack armor : player.getArmorSlots()) {
            if (armor.getItem() instanceof ArmorItem armorItem) {
                EquipmentSlot slot = armorItem.getEquipmentSlot();
                if (slot == EquipmentSlot.CHEST || slot == EquipmentSlot.LEGS) {
                    return true;
                }
            }
        }
        return false;
    }
}
