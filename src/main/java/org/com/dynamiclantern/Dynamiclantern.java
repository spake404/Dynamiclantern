package org.com.dynamiclantern;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.com.dynamiclantern.client.DynamiclanternClient;

@Mod(Dynamiclantern.MODID)
public class Dynamiclantern {
    public static final String MODID = "dynamiclantern";

    public Dynamiclantern(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            IConfigScreenFactory configScreenFactory = DynamiclanternClient.configScreenFactory();
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, configScreenFactory);
        }
        NeoForge.EVENT_BUS.register(CuriosLanternCache.class);
    }

    public static boolean isLantern(ItemStack stack) {
        return stack.is(Items.LANTERN) || stack.is(Items.SOUL_LANTERN);
    }
}
