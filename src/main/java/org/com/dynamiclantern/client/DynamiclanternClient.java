package org.com.dynamiclantern.client;

import net.minecraft.world.item.Items;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

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
    }

    public static IConfigScreenFactory configScreenFactory() {
        return (container, parent) -> new DynamiclanternConfigScreen(parent);
    }
}
