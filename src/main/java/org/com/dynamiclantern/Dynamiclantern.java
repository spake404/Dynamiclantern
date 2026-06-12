package org.com.dynamiclantern;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.com.dynamiclantern.client.DynamiclanternClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.theillusivec4.curios.api.CuriosApi;

@Mod(Dynamiclantern.MODID)
public class Dynamiclantern {
    public static final String MODID = "dynamiclantern";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public Dynamiclantern() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> DynamiclanternClient.registerOptionalModListeners(modEventBus));
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
        CuriosApi.registerCurioPredicate(
                new ResourceLocation(MODID, "waist_renderable"),
                slotResult -> WaistItemRules.isBeltSlot(slotResult.slotContext())
                        && WaistItemRules.isRenderableWaistItem(slotResult.stack()));
        MinecraftForge.EVENT_BUS.register(WaistItemEvents.class);
        if (ModList.get().isLoaded("cold_sweat")) {
            MinecraftForge.EVENT_BUS.register(ColdSweatSoulspringCompat.class);
        }
    }
}
