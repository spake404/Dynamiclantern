package org.com.dynamiclantern.client;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import net.minecraft.world.item.Items;

public final class DynamiclanternClient {
    private DynamiclanternClient() {
    }

    public static void init() {
        if (ModList.get().isLoaded("epicfight")) {
            CuriosRendererRegistry.register(Items.LANTERN, EpicFightCurioLanternRenderer::new);
            CuriosRendererRegistry.register(Items.SOUL_LANTERN, EpicFightCurioLanternRenderer::new);
        } else {
            CuriosRendererRegistry.register(Items.LANTERN, CurioLanternRenderer::new);
            CuriosRendererRegistry.register(Items.SOUL_LANTERN, CurioLanternRenderer::new);
        }
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, parent) -> new DynamiclanternConfigScreen(parent)));
    }
}
