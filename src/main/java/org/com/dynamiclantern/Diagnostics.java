package org.com.dynamiclantern;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Diagnostics {
    private static final long LOG_INTERVAL_MILLIS = 1000L;
    private static final Map<String, Long> LAST_LOG_TIMES = new ConcurrentHashMap<>();

    private Diagnostics() {
    }

    public static boolean isInteresting(ItemStack stack) {
        return isEnabled()
                && !stack.isEmpty()
                && (WaistItemRules.isColdSweatSoulspringLamp(stack)
                || WaistItemRules.isRenderableWaistItem(stack));
    }

    public static void log(String key, String message, Object... args) {
        if (!isEnabled()) {
            return;
        }

        long now = System.currentTimeMillis();
        Long last = LAST_LOG_TIMES.get(key);
        if (last != null && now - last < LOG_INTERVAL_MILLIS) {
            return;
        }

        LAST_LOG_TIMES.put(key, now);
        Dynamiclantern.LOGGER.info("[RenderDiag] " + message, args);
    }

    private static boolean isEnabled() {
        try {
            return Config.RENDER_DIAGNOSTIC_LOG.get();
        } catch (IllegalStateException e) {
            // Config not yet loaded (e.g. during mod construction); diagnostics default to off.
            return false;
        }
    }

    public static String itemId(ItemStack stack) {
        return stack.isEmpty() ? "empty" : WaistItemRules.itemId(stack.getItem());
    }

    public static String playerName(Player player) {
        return player == null ? "none" : player.getGameProfile().getName();
    }

    public static String slot(SlotContext slotContext) {
        if (slotContext == null) {
            return "none";
        }
        return slotContext.identifier()
                + "#" + slotContext.index()
                + ",cosmetic=" + slotContext.cosmetic()
                + ",visible=" + slotContext.visible();
    }
}
