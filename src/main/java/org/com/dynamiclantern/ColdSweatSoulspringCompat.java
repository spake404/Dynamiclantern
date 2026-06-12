package org.com.dynamiclantern;

import com.momosoftworks.coldsweat.common.item.SoulspringLampItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import java.util.Objects;
import java.util.Optional;

public final class ColdSweatSoulspringCompat {
    private static final ResourceLocation SOULSPRING_LAMP_ID = Objects.requireNonNull(ResourceLocation.tryParse("cold_sweat:soulspring_lamp"));
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
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!COLD_SWEAT_LOADED
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
    public static void onLivingAttack(LivingIncomingDamageEvent event) {
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
                || target.getType().is(EntityTypeTags.UNDEAD)
                || target.getPersistentData().getBoolean(SOUL_SUCKED_TAG)) {
            return;
        }

        target.getPersistentData().putBoolean(SOUL_SUCKED_TAG, true);
        double fuelGained = (int) Math.min(8.0F, target.getMaxHealth() / 2.0F);
        addFuel(lamp, fuelGained);

        float originalAmount = event.getAmount();
        event.setAmount(Math.max(originalAmount, 8.0F));

        if (isDebugLogEnabled()) {
            Dynamiclantern.LOGGER.info(
                    "[SoulspringCompat] Curios attack effect applied: player={}, slot={}#{}, target={}, fuelGained={}, fuel={}, extraDamage={}",
                    player.getGameProfile().getName(),
                    result.get().slotContext().identifier(),
                    result.get().slotContext().index(),
                    BuiltInRegistries.ENTITY_TYPE.getKey(target.getType()),
                    fuelGained,
                    getFuel(lamp),
                    event.getAmount() - originalAmount);
        }
    }

    private static Optional<SlotResult> findCurioSoulspringLamp(Player player) {
        if (!COLD_SWEAT_LOADED) {
            return Optional.empty();
        }

        return CuriosApi.getCuriosInventory(player)
                .flatMap(handler -> handler.findFirstCurio(ColdSweatSoulspringCompat::isSoulspringLamp));
    }

    private static boolean isSoulspringLamp(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == soulspringLampItem();
    }

    private static boolean isLit(ItemStack stack) {
        return SoulspringLampItem.isLit(stack);
    }

    private static double getFuel(ItemStack stack) {
        return SoulspringLampItem.getFuel(stack);
    }

    private static void addFuel(ItemStack stack, double amount) {
        SoulspringLampItem.addFuel(stack, amount);
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
                Item resolved = BuiltInRegistries.ITEM.get(SOULSPRING_LAMP_ID);
                soulspringLampItem = resolved == null ? Items.AIR : resolved;
            }
            return soulspringLampItem;
        }
    }
}
