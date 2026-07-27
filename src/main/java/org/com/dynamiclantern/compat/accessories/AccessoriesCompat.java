package org.com.dynamiclantern.compat.accessories;

import com.google.common.collect.HashMultimap;
import io.wispforest.accessories.Accessories;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.events.AccessoryChangeCallback;
import io.wispforest.accessories.api.events.ContainersChangeCallback;
import io.wispforest.accessories.api.slot.SlotReference;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.com.dynamiclantern.Dynamiclantern;
import org.com.dynamiclantern.WaistItemCache;
import org.com.dynamiclantern.WaistItemRules;
import org.com.dynamiclantern.WaistSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class AccessoriesCompat {
    private static final ResourceLocation WAIST_RENDERABLE_PREDICATE =
            ResourceLocation.fromNamespaceAndPath(Dynamiclantern.MODID, "waist_renderable");
    private static final ResourceLocation LANTERN_SLOT_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(Dynamiclantern.MODID, "accessories_lantern_slot");
    private static boolean initialized;

    private AccessoriesCompat() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        AccessoriesAPI.registerPredicate(WAIST_RENDERABLE_PREDICATE, (level, slotType, index, stack) -> {
            if (!WaistItemRules.isKnownWaistItemSlot(slotType.name())) {
                return TriState.DEFAULT;
            }
            if (WaistItemRules.LANTERN_SLOT.equals(slotType.name()) && !WaistItemRules.isLanternSlotEnabled()) {
                return TriState.FALSE;
            }
            return WaistItemRules.isRenderableWaistItem(stack) ? TriState.TRUE : TriState.DEFAULT;
        });

        AccessoryChangeCallback.EVENT.register((previousStack, currentStack, reference, stateChange) -> {
            if (reference.entity() instanceof Player player
                    && WaistItemRules.isKnownWaistItemSlot(reference.slotName())) {
                WaistItemCache.clear(player);
            }
        });
        ContainersChangeCallback.EVENT.register((entity, capability, changedContainers) -> {
            if (entity instanceof Player player
                    && changedContainers.keySet().stream()
                    .map(AccessoriesContainer::getSlotName)
                    .anyMatch(WaistItemRules::isKnownWaistItemSlot)) {
                WaistItemCache.clear(player);
            }
        });
        WaistItemCache.registerSource(AccessoriesCompat::findVisibleWaistItems);
    }

    public static void syncLanternSlot(Player player) {
        if (!(player instanceof ServerPlayer)) {
            return;
        }

        AccessoriesCapability capability = AccessoriesCapability.get(player);
        if (capability == null || !capability.getContainers().containsKey(WaistItemRules.LANTERN_SLOT)) {
            return;
        }

        var modifiers = HashMultimap.<String, AttributeModifier>create();
        modifiers.put(
                WaistItemRules.LANTERN_SLOT,
                new AttributeModifier(LANTERN_SLOT_MODIFIER_ID, 1.0D, AttributeModifier.Operation.ADD_VALUE));
        capability.removeSlotModifiers(modifiers);
        if (WaistItemRules.isLanternSlotEnabled()) {
            capability.addTransientSlotModifiers(modifiers);
        }
        capability.updateContainers();
        WaistItemCache.clear(player);
    }

    public static Optional<EquippedItem> findFirstManagedItem(Player player, Predicate<ItemStack> predicate) {
        AccessoriesCapability capability = AccessoriesCapability.get(player);
        if (capability == null) {
            return Optional.empty();
        }

        for (String slotId : WaistItemRules.renderSlotIdsByPriority()) {
            AccessoriesContainer container = capability.getContainers().get(slotId);
            if (container == null) {
                continue;
            }
            for (int index = 0; index < container.getAccessories().getContainerSize(); index++) {
                ItemStack stack = container.getAccessories().getItem(index);
                if (!stack.isEmpty() && predicate.test(stack)) {
                    return Optional.of(new EquippedItem(stack, slotId, index));
                }
            }
        }
        return Optional.empty();
    }

    private static List<WaistItemCache.CachedItem> findVisibleWaistItems(Player player) {
        AccessoriesCapability capability = AccessoriesCapability.get(player);
        if (capability == null) {
            return List.of();
        }

        List<WaistItemCache.CachedItem> items = new ArrayList<>();
        for (String slotId : WaistItemRules.renderSlotIdsByPriority()) {
            AccessoriesContainer container = capability.getContainers().get(slotId);
            if (container == null) {
                continue;
            }

            int size = container.getAccessories().getContainerSize();
            for (int index = 0; index < size; index++) {
                if (!container.shouldRender(index)) {
                    continue;
                }

                ItemStack stack = container.getAccessories().getItem(index);
                boolean cosmetic = false;
                if (player.level().isClientSide() && Accessories.config().clientOptions.showCosmeticAccessories()) {
                    ItemStack cosmeticStack = container.getCosmeticAccessories().getItem(index);
                    if (!cosmeticStack.isEmpty()) {
                        stack = cosmeticStack;
                        cosmetic = true;
                    }
                }
                if (!WaistItemRules.isRenderableWaistItem(stack)) {
                    continue;
                }

                WaistSlot slot = new WaistSlot(
                        WaistSlot.Source.ACCESSORIES,
                        slotId,
                        index,
                        cosmetic,
                        true);
                items.add(new WaistItemCache.CachedItem(stack, slot));
            }
        }
        return List.copyOf(items);
    }

    public record EquippedItem(ItemStack stack, String slotName, int index) {
    }
}
