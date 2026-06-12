package org.com.dynamiclantern;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.com.dynamiclantern.client.DynamiclanternClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Dynamiclantern.MODID)
public class Dynamiclantern {
    public static final String MODID = "dynamiclantern";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public Dynamiclantern(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            IConfigScreenFactory configScreenFactory = DynamiclanternClient.configScreenFactory();
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, configScreenFactory);
            DynamiclanternClient.registerOptionalModListeners(modEventBus);
        }
        NeoForge.EVENT_BUS.register(WaistItemEvents.class);
        if (ModList.get().isLoaded("cold_sweat")) {
            NeoForge.EVENT_BUS.register(ColdSweatSoulspringCompat.class);
        }
    }
}
