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
        MinecraftForge.EVENT_BUS.register(CuriosLanternCache.class);
    }

    public static boolean isLantern(net.minecraft.world.item.ItemStack stack) {
        return stack.is(net.minecraft.world.item.Items.LANTERN) || stack.is(net.minecraft.world.item.Items.SOUL_LANTERN);
    }
}
