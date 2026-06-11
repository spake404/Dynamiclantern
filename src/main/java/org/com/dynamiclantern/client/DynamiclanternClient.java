package org.com.dynamiclantern.client;

import net.minecraft.world.item.Item;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.ModList;
import org.com.dynamiclantern.WaistItemRules;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class DynamiclanternClient {
    private static final Set<Item> REGISTERED_RENDERERS = ConcurrentHashMap.newKeySet();

    private DynamiclanternClient() {
    }

    public static void init() {
        registerConfiguredRenderers();
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, parent) -> new DynamiclanternConfigScreen(parent)));
    }

    public static void registerConfiguredRenderers() {
        boolean changed = false;
        for (Item item : WaistItemRules.getConfiguredItems()) {
            changed |= registerRenderer(item);
        }
        if (changed) {
            CuriosRendererRegistry.load();
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
        return true;
    }

    private static ICurioRenderer createRenderer() {
        return ModList.get().isLoaded("epicfight")
                ? new EpicFightCurioWaistItemRenderer()
                : new CurioWaistItemRenderer();
    }
}
