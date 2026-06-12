package org.com.dynamiclantern;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import java.util.Objects;
import java.util.Optional;

public final class ColdSweatSoulspringCompat {
    private static final ResourceLocation SOULSPRING_LAMP_ID = Objects.requireNonNull(ResourceLocation.tryParse("cold_sweat:soulspring_lamp"));
    private static final String FUEL_TAG = "Fuel";
    private static final String LIT_TAG = "Lit";
    private static final String SOUL_SUCKED_TAG = "SoulSucked";
    private static final double MAX_FUEL = 64.0D;
    private static final int DIAGNOSTIC_INTERVAL_TICKS = 100;
    private static final boolean COLD_SWEAT_LOADED = isModLoaded("cold_sweat");
    private static volatile Item soulspringLampItem;

    private ColdSweatSoulspringCompat() {
    }

    @SubscribeEvent
    public static void onCurioChange(CurioChangeEvent event) {
        if (!COLD_SWEAT_LOADED || !isDebugLogEnabled() || !(event.getEntity() instanceof Player player)) {
            return;
        }

        boolean fromLamp = isSoulspringLamp(event.getFrom());
        boolean toLamp = isSoulspringLamp(event.getTo());
        if (!fromLamp && !toLamp) {
            return;
        }

        ItemStack stack = toLamp ? event.getTo() : event.getFrom();
        Dynamiclantern.LOGGER.info(
                "[SoulspringCompat] Curio change: player={}, slot={}#{}, equipped={}, lit={}, fuel={}, waistRenderable={}, shaderLight={}",
                player.getGameProfile().getName(),
                event.getIdentifier(),
                event.getSlotIndex(),
                toLamp,
                isLit(stack),
                getFuel(stack),
                WaistItemRules.isRenderableWaistItem(stack),
                WaistItemRules.isShaderLightItem(stack));
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (!COLD_SWEAT_LOADED
                || event.phase != TickEvent.Phase.END
                || player.level().isClientSide
                || player.tickCount % DIAGNOSTIC_INTERVAL_TICKS != 0
                || !isDebugLogEnabled()) {
            return;
        }

        findCurioSoulspringLamp(player).ifPresent(result -> {
            ItemStack stack = result.stack();
            Dynamiclantern.LOGGER.info(
                    "[SoulspringCompat] Curios lamp detected: player={}, slot={}#{}, lit={}, fuel={}, waistRenderable={}, shaderLight={}",
                    player.getGameProfile().getName(),
                    result.slotContext().identifier(),
                    result.slotContext().index(),
                    isLit(stack),
                    getFuel(stack),
                    WaistItemRules.isRenderableWaistItem(stack),
                    WaistItemRules.isShaderLightItem(stack));
        });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        Entity attacker = event.getSource().getEntity();
        if (!COLD_SWEAT_LOADED
                || !(attacker instanceof Player player)
                || event.getEntity() instanceof Player
                || isSoulspringLamp(player.getMainHandItem())) {
            return;
        }

        Optional<SlotResult> result = findCurioSoulspringLamp(player);
        if (result.isEmpty()) {
            return;
        }

        LivingEntity target = event.getEntity();
        ItemStack lamp = result.get().stack();
        if (getFuel(lamp) >= MAX_FUEL
                || target.getMobType() == MobType.UNDEAD
                || target.getPersistentData().getBoolean(SOUL_SUCKED_TAG)) {
            return;
        }

        target.getPersistentData().putBoolean(SOUL_SUCKED_TAG, true);
        double fuelGained = Math.min(8.0F, target.getMaxHealth() / 2.0F);
        addFuel(lamp, fuelGained);

        float extraDamage = Math.max(0.0F, 8.0F - event.getAmount());
        if (extraDamage > 0.0F) {
            target.hurt(player.level().damageSources().playerAttack(player), extraDamage);
        }

        if (isDebugLogEnabled()) {
            Dynamiclantern.LOGGER.info(
                    "[SoulspringCompat] Curios attack effect applied: player={}, slot={}#{}, target={}, fuelGained={}, fuel={}, extraDamage={}",
                    player.getGameProfile().getName(),
                    result.get().slotContext().identifier(),
                    result.get().slotContext().index(),
                    ForgeRegistries.ENTITY_TYPES.getKey(target.getType()),
                    fuelGained,
                    getFuel(lamp),
                    extraDamage);
        }
    }

    private static Optional<SlotResult> findCurioSoulspringLamp(Player player) {
        if (!COLD_SWEAT_LOADED) {
            return Optional.empty();
        }

        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(handler -> handler.findFirstCurio(ColdSweatSoulspringCompat::isSoulspringLamp));
    }

    private static boolean isSoulspringLamp(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == soulspringLampItem();
    }

    private static boolean isLit(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(LIT_TAG);
    }

    private static double getFuel(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag == null ? 0.0D : tag.getDouble(FUEL_TAG);
    }

    private static void addFuel(ItemStack stack, double amount) {
        stack.getOrCreateTag().putDouble(FUEL_TAG, Math.min(MAX_FUEL, getFuel(stack) + amount));
    }

    private static boolean isDebugLogEnabled() {
        return Config.SOULSPRING_LAMP_DEBUG_LOG.get();
    }

    private static boolean isModLoaded(String modid) {
        return ModList.get() != null && ModList.get().isLoaded(modid);
    }

    private static Item soulspringLampItem() {
        Item item = soulspringLampItem;
        if (item != null) {
            return item;
        }

        synchronized (ColdSweatSoulspringCompat.class) {
            if (soulspringLampItem == null) {
                Item resolved = ForgeRegistries.ITEMS.getValue(SOULSPRING_LAMP_ID);
                soulspringLampItem = resolved == null ? Items.AIR : resolved;
            }
            return soulspringLampItem;
        }
    }
}
