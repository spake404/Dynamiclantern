package org.com.dynamiclantern.client;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.com.dynamiclantern.Diagnostics;
import org.com.dynamiclantern.WaistItemRules;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.util.HashSet;
import java.util.Set;

public final class DynamiclanternClient {
    private static final Set<Item> REGISTERED_RENDERERS = new HashSet<>();

    private DynamiclanternClient() {
    }

    public static void init() {
        registerConfiguredRenderers();
    }

    public static IConfigScreenFactory configScreenFactory() {
        return (container, parent) -> new DynamiclanternConfigScreen(parent);
    }

    public static void registerOptionalModListeners(IEventBus modEventBus) {
        Diagnostics.log(
                "client-optional-mods",
                "optional mods epicfight={}, epicfight_curios_compat={}, cold_sweat={}",
                ModList.get().isLoaded("epicfight"),
                ModList.get().isLoaded("epicfight_curios_compat"),
                ModList.get().isLoaded("cold_sweat"));
        if (ModList.get().isLoaded("epicfight")) {
            EpicFightWaistItemLayer.register(modEventBus);
        }
    }

    public static void registerConfiguredRenderers() {
        boolean changed = false;
        for (Item item : WaistItemRules.getRenderableItems()) {
            changed |= registerRenderer(item);
        }
        if (changed) {
            CuriosRendererRegistry.load();
            Diagnostics.log(
                    "client-renderer-load",
                    "registered/loaded Dynamic Lantern Curios renderers, totalRegistered={}",
                    REGISTERED_RENDERERS.size());
        }
    }

    public static void registerConfiguredRenderer(Item item) {
        if (registerRenderer(item)) {
            CuriosRendererRegistry.load();
        }
    }

    private static boolean registerRenderer(Item item) {
        if (!REGISTERED_RENDERERS.add(item)) {
            return false;
        }

        CuriosRendererRegistry.register(item, DynamiclanternClient::createRenderer);
        Diagnostics.log(
                "client-renderer-register-" + WaistItemRules.itemId(item),
                "registered Curios renderer for item={}",
                WaistItemRules.itemId(item));
        return true;
    }

    private static ICurioRenderer createRenderer() {
        return new CurioWaistItemRenderer();
    }
}
