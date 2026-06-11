package org.com.dynamiclantern;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(Dynamiclantern.MODID)
public class Dynamiclantern {
    public static final String MODID = "dynamiclantern";

    public Dynamiclantern() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
        MinecraftForge.EVENT_BUS.register(WaistItemEvents.class);
    }
}
