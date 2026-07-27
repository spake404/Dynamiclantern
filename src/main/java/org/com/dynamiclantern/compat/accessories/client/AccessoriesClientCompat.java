package org.com.dynamiclantern.compat.accessories.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.com.dynamiclantern.Diagnostics;
import org.com.dynamiclantern.WaistItemRules;
import org.com.dynamiclantern.WaistSlot;
import org.com.dynamiclantern.client.CurioWaistItemRenderer;

import java.util.HashSet;
import java.util.Set;

public final class AccessoriesClientCompat {
    private static final Set<Item> REGISTERED_RENDERERS = new HashSet<>();

    private AccessoriesClientCompat() {
    }

    public static boolean registerRenderer(Item item) {
        if (!REGISTERED_RENDERERS.add(item)) {
            return false;
        }
        AccessoriesRendererRegistry.registerRenderer(item, WaistRenderer::new);
        Diagnostics.log(
                "client-accessories-renderer-register-" + WaistItemRules.itemId(item),
                "registered Accessories renderer for item={}",
                WaistItemRules.itemId(item));
        return true;
    }

    public static void reloadRenderers() {
        AccessoriesRendererRegistry.onReload();
    }

    private static final class WaistRenderer implements AccessoryRenderer {
        @Override
        public <M extends LivingEntity> void render(
                ItemStack stack,
                SlotReference reference,
                PoseStack poseStack,
                EntityModel<M> model,
                MultiBufferSource buffers,
                int packedLight,
                float limbSwing,
                float limbSwingAmount,
                float partialTicks,
                float ageInTicks,
                float netHeadYaw,
                float headPitch) {
            if (!(reference.entity() instanceof Player player)) {
                return;
            }

            var container = reference.slotContainer();
            boolean visible = container != null && container.shouldRender(reference.slot());
            ItemStack cosmeticStack = container == null
                    ? ItemStack.EMPTY
                    : container.getCosmeticAccessories().getItem(reference.slot());
            boolean cosmetic = !cosmeticStack.isEmpty() && ItemStack.matches(stack, cosmeticStack);
            WaistSlot slot = new WaistSlot(
                    WaistSlot.Source.ACCESSORIES,
                    reference.slotName(),
                    reference.slot(),
                    cosmetic,
                    visible);
            CurioWaistItemRenderer.renderWaistItem(
                    stack,
                    slot,
                    player,
                    poseStack,
                    model,
                    buffers,
                    packedLight,
                    limbSwing,
                    limbSwingAmount,
                    partialTicks,
                    ageInTicks,
                    netHeadYaw,
                    headPitch);
        }
    }
}
