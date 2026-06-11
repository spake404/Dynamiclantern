package org.com.dynamiclantern;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class WaistItemRules {
    public static final String BELT_SLOT = "belt";
    public static final String COLD_SWEAT_SOULSPRING_LAMP_ID = "cold_sweat:soulspring_lamp";
    public static final List<String> BUILT_IN_DEFAULT_ITEM_IDS = List.of(
            "minecraft:lantern",
            "minecraft:soul_lantern",
            "under_the_moon:moon_lamp",
            COLD_SWEAT_SOULSPRING_LAMP_ID,
            "skinnedlanterns:bee_lantern_block",
            "skinnedlanterns:bee_soul_lantern_block",
            "skinnedlanterns:blinky_lantern_block",
            "skinnedlanterns:blinky_soul_lantern_block",
            "skinnedlanterns:blue_jellyfish_lantern_block",
            "skinnedlanterns:blue_jellyfish_soul_lantern_block",
            "skinnedlanterns:clyde_lantern_block",
            "skinnedlanterns:clyde_soul_lantern_block",
            "skinnedlanterns:creeper_lantern_block",
            "skinnedlanterns:creeper_soul_lantern_block",
            "skinnedlanterns:creeper_ender_lantern_block",
            "skinnedlanterns:ghost_lantern_block",
            "skinnedlanterns:ghost_soul_lantern_block",
            "skinnedlanterns:guardian_lantern_block",
            "skinnedlanterns:guardian_soul_lantern_block",
            "skinnedlanterns:honey_lantern_block",
            "skinnedlanterns:honey_soul_lantern_block",
            "skinnedlanterns:inky_lantern_block",
            "skinnedlanterns:inky_soul_lantern_block",
            "skinnedlanterns:jack_o_lantern_lantern_block",
            "skinnedlanterns:jack_o_lantern_soul_lantern_block",
            "skinnedlanterns:jack_o_lantern_ender_lantern_block",
            "skinnedlanterns:jellyfish_lantern_block",
            "skinnedlanterns:jellyfish_soul_lantern_block",
            "skinnedlanterns:ornament_black_lantern_block",
            "skinnedlanterns:ornament_black_soul_lantern_block",
            "skinnedlanterns:ornament_blue_lantern_block",
            "skinnedlanterns:ornament_blue_soul_lantern_block",
            "skinnedlanterns:ornament_brown_lantern_block",
            "skinnedlanterns:ornament_brown_soul_lantern_block",
            "skinnedlanterns:ornament_cyan_lantern_block",
            "skinnedlanterns:ornament_cyan_soul_lantern_block",
            "skinnedlanterns:ornament_gray_lantern_block",
            "skinnedlanterns:ornament_gray_soul_lantern_block",
            "skinnedlanterns:ornament_green_lantern_block",
            "skinnedlanterns:ornament_green_soul_lantern_block",
            "skinnedlanterns:ornament_light_blue_lantern_block",
            "skinnedlanterns:ornament_light_blue_soul_lantern_block",
            "skinnedlanterns:ornament_light_gray_lantern_block",
            "skinnedlanterns:ornament_light_gray_soul_lantern_block",
            "skinnedlanterns:ornament_lime_lantern_block",
            "skinnedlanterns:ornament_lime_soul_lantern_block",
            "skinnedlanterns:ornament_magenta_lantern_block",
            "skinnedlanterns:ornament_magenta_soul_lantern_block",
            "skinnedlanterns:ornament_orange_lantern_block",
            "skinnedlanterns:ornament_orange_soul_lantern_block",
            "skinnedlanterns:ornament_pink_lantern_block",
            "skinnedlanterns:ornament_pink_soul_lantern_block",
            "skinnedlanterns:ornament_purple_lantern_block",
            "skinnedlanterns:ornament_purple_soul_lantern_block",
            "skinnedlanterns:ornament_red_lantern_block",
            "skinnedlanterns:ornament_red_soul_lantern_block",
            "skinnedlanterns:ornament_white_lantern_block",
            "skinnedlanterns:ornament_white_soul_lantern_block",
            "skinnedlanterns:ornament_yellow_lantern_block",
            "skinnedlanterns:ornament_yellow_soul_lantern_block",
            "skinnedlanterns:pacman_lantern_block",
            "skinnedlanterns:pacman_soul_lantern_block",
            "skinnedlanterns:paper_black_lantern_block",
            "skinnedlanterns:paper_black_soul_lantern_block",
            "skinnedlanterns:paper_blue_lantern_block",
            "skinnedlanterns:paper_blue_soul_lantern_block",
            "skinnedlanterns:paper_brown_lantern_block",
            "skinnedlanterns:paper_brown_soul_lantern_block",
            "skinnedlanterns:paper_cyan_lantern_block",
            "skinnedlanterns:paper_cyan_soul_lantern_block",
            "skinnedlanterns:paper_gray_lantern_block",
            "skinnedlanterns:paper_gray_soul_lantern_block",
            "skinnedlanterns:paper_green_lantern_block",
            "skinnedlanterns:paper_green_soul_lantern_block",
            "skinnedlanterns:paper_lantern_block",
            "skinnedlanterns:paper_soul_lantern_block",
            "skinnedlanterns:paper_light_blue_lantern_block",
            "skinnedlanterns:paper_light_blue_soul_lantern_block",
            "skinnedlanterns:paper_light_gray_lantern_block",
            "skinnedlanterns:paper_light_gray_soul_lantern_block",
            "skinnedlanterns:paper_lime_lantern_block",
            "skinnedlanterns:paper_lime_soul_lantern_block",
            "skinnedlanterns:paper_magenta_lantern_block",
            "skinnedlanterns:paper_magenta_soul_lantern_block",
            "skinnedlanterns:paper_orange_lantern_block",
            "skinnedlanterns:paper_orange_soul_lantern_block",
            "skinnedlanterns:paper_pink_lantern_block",
            "skinnedlanterns:paper_pink_soul_lantern_block",
            "skinnedlanterns:paper_purple_lantern_block",
            "skinnedlanterns:paper_purple_soul_lantern_block",
            "skinnedlanterns:paper_white_lantern_block",
            "skinnedlanterns:paper_white_soul_lantern_block",
            "skinnedlanterns:paper_yellow_lantern_block",
            "skinnedlanterns:paper_yellow_soul_lantern_block",
            "skinnedlanterns:pinky_lantern_block",
            "skinnedlanterns:pinky_soul_lantern_block",
            "skinnedlanterns:present_green_lantern_block",
            "skinnedlanterns:present_green_soul_lantern_block",
            "skinnedlanterns:present_red_lantern_block",
            "skinnedlanterns:present_red_soul_lantern_block",
            "skinnedlanterns:pufferfish_lantern_block",
            "skinnedlanterns:pufferfish_soul_lantern_block",
            "skinnedlanterns:skeleton_lantern_block",
            "skinnedlanterns:skeleton_soul_lantern_block",
            "skinnedlanterns:skeleton_ender_lantern_block",
            "skinnedlanterns:slime_lantern_block",
            "skinnedlanterns:slime_soul_lantern_block",
            "skinnedlanterns:snowman_lantern_block",
            "skinnedlanterns:snowman_soul_lantern_block",
            "skinnedlanterns:tiny_potato_lantern_block",
            "skinnedlanterns:tiny_potato_soul_lantern_block",
            "skinnedlanterns:wither_skeleton_lantern_block",
            "skinnedlanterns:wither_skeleton_soul_lantern_block",
            "skinnedlanterns:wither_skeleton_ender_lantern_block",
            "skinnedlanterns:zombie_lantern_block",
            "skinnedlanterns:zombie_soul_lantern_block",
            "skinnedlanterns:zombie_ender_lantern_block");
    public static final List<String> BUILT_IN_SHADER_LIGHT_ITEM_IDS = List.of(
            COLD_SWEAT_SOULSPRING_LAMP_ID);
    private static final Set<String> BUILT_IN_DEFAULT_ITEM_ID_SET = Set.copyOf(BUILT_IN_DEFAULT_ITEM_IDS);
    private static final Set<String> BUILT_IN_SHADER_LIGHT_ITEM_ID_SET = Set.copyOf(BUILT_IN_SHADER_LIGHT_ITEM_IDS);

    private static volatile List<String> cachedIds = List.of();
    private static volatile Set<Item> cachedItems = Set.of();

    private WaistItemRules() {
    }

    public static boolean isBeltSlot(SlotContext slotContext) {
        return slotContext != null && BELT_SLOT.equals(slotContext.identifier());
    }

    public static boolean canEquipInBelt(ItemStack stack, SlotContext slotContext) {
        return isBeltSlot(slotContext) && isRenderableWaistItem(stack);
    }

    public static boolean isConfiguredWaistItem(ItemStack stack) {
        return !stack.isEmpty() && configuredItems().contains(stack.getItem());
    }

    public static boolean isRenderableWaistItem(ItemStack stack) {
        return !stack.isEmpty() && renderableItems().contains(stack.getItem());
    }

    public static boolean isShaderLightItem(ItemStack stack) {
        return !stack.isEmpty() && (isLightEmittingBlockItem(stack) || isBuiltInShaderLightItem(stack));
    }

    public static ItemStack shaderLightStack(ItemStack stack) {
        if (isBuiltInShaderLightItem(stack)) {
            return new ItemStack(Items.SOUL_LANTERN);
        }

        return stack;
    }

    public static boolean isBlockItem(ItemStack stack) {
        return blockByItem(stack) != Blocks.AIR;
    }

    public static Block blockByItem(ItemStack stack) {
        return stack.isEmpty() ? Blocks.AIR : Block.byItem(stack.getItem());
    }

    public static List<String> getConfiguredItemIds() {
        return normalizeIds(Config.WAIST_RENDERABLE_ITEMS.get());
    }

    public static List<String> getRenderableItemIds() {
        List<String> ids = new ArrayList<>(BUILT_IN_DEFAULT_ITEM_IDS);
        ids.addAll(getConfiguredItemIds());
        return normalizeIds(ids);
    }

    public static Set<Item> getConfiguredItems() {
        return configuredItems();
    }

    public static Set<Item> getRenderableItems() {
        return renderableItems();
    }

    public static Optional<Item> resolveItem(String rawId) {
        String id = normalizeId(rawId);
        if (id.isEmpty()) {
            return Optional.empty();
        }

        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location == null || !ForgeRegistries.ITEMS.containsKey(location)) {
            return Optional.empty();
        }

        Item item = ForgeRegistries.ITEMS.getValue(location);
        return item == null || item == Items.AIR ? Optional.empty() : Optional.of(item);
    }

    public static Optional<Item> addConfiguredItem(String rawId) {
        Optional<Item> item = resolveItem(rawId);
        if (item.isEmpty()) {
            return Optional.empty();
        }

        String id = itemId(item.get());
        List<String> ids = new ArrayList<>(getConfiguredItemIds());
        if (!ids.contains(id) && !isBuiltInDefaultItemId(id)) {
            ids.add(id);
            setConfiguredItemIds(ids);
        }
        return item;
    }

    public static boolean removeConfiguredItem(String rawId) {
        String id = normalizeId(rawId);
        List<String> ids = new ArrayList<>(getConfiguredItemIds());
        boolean removed = ids.remove(id);
        if (removed) {
            setConfiguredItemIds(ids);
        }
        return removed;
    }

    public static String itemId(Item item) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        return key == null ? "" : key.toString();
    }

    public static void invalidate() {
        cachedIds = List.of();
        cachedItems = Set.of();
    }

    public static boolean isLightEmittingBlockItem(ItemStack stack) {
        Block block = blockByItem(stack);
        return block != Blocks.AIR && block.defaultBlockState().getLightEmission() > 0;
    }

    public static boolean isColdSweatSoulspringLamp(ItemStack stack) {
        return !stack.isEmpty() && COLD_SWEAT_SOULSPRING_LAMP_ID.equals(itemId(stack.getItem()));
    }

    private static void setConfiguredItemIds(Collection<String> ids) {
        Config.WAIST_RENDERABLE_ITEMS.set(normalizeIds(ids));
        invalidate();
        WaistItemCache.clearAll();
    }

    private static Set<Item> configuredItems() {
        List<String> ids = getConfiguredItemIds();
        return resolveItems(ids);
    }

    private static Set<Item> renderableItems() {
        List<String> ids = getRenderableItemIds();
        if (!ids.equals(cachedIds)) {
            synchronized (WaistItemRules.class) {
                if (!ids.equals(cachedIds)) {
                    Set<Item> items = resolveItems(ids);
                    cachedIds = ids;
                    cachedItems = items;
                }
            }
        }
        return cachedItems;
    }

    private static Set<Item> resolveItems(Collection<String> ids) {
        Set<Item> items = new HashSet<>();
        for (String id : ids) {
            resolveItem(id).ifPresent(items::add);
        }
        return Set.copyOf(items);
    }

    private static boolean isBuiltInDefaultItemId(String id) {
        return BUILT_IN_DEFAULT_ITEM_ID_SET.contains(normalizeId(id));
    }

    private static boolean isBuiltInShaderLightItem(ItemStack stack) {
        return !stack.isEmpty() && BUILT_IN_SHADER_LIGHT_ITEM_ID_SET.contains(itemId(stack.getItem()));
    }

    private static List<String> normalizeIds(Collection<? extends String> ids) {
        Set<String> seen = new HashSet<>();
        List<String> normalized = new ArrayList<>();
        for (String id : ids) {
            String normalizedId = normalizeId(id);
            if (!normalizedId.isEmpty() && seen.add(normalizedId)) {
                normalized.add(normalizedId);
            }
        }
        return List.copyOf(normalized);
    }

    private static String normalizeId(String rawId) {
        return Objects.toString(rawId, "").trim();
    }
}
