package org.com.dynamiclantern.client;

import net.minecraftforge.fml.ModList;

public final class EpicFightCuriosFallbackGuard {
    private static final String EPIC_FIGHT_CURIOS_LAYER = "yesman.epicfight.compat.CuriosCompat$PatchedCuriosLayerRenderer";
    private static final String EPIC_FIGHT_CURIOS_COMPAT_LAYER = "com.oneworldstudio.epicfightcurioscompat.ClientCuriosCompat$PatchedCuriosLayerRenderer";
    private static final boolean EPIC_FIGHT_LOADED = isModLoaded("epicfight");
    private static final boolean EPIC_FIGHT_CURIOS_COMPAT_LOADED = isModLoaded("epicfight_curios_compat");

    private EpicFightCuriosFallbackGuard() {
    }

    public static boolean isSuppressedLayerCall() {
        return (EPIC_FIGHT_LOADED || EPIC_FIGHT_CURIOS_COMPAT_LOADED) && isEpicFightCuriosFallback();
    }

    private static boolean isEpicFightCuriosFallback() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            String className = element.getClassName();
            if ((EPIC_FIGHT_LOADED && EPIC_FIGHT_CURIOS_LAYER.equals(className))
                    || (EPIC_FIGHT_CURIOS_COMPAT_LOADED && EPIC_FIGHT_CURIOS_COMPAT_LAYER.equals(className))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isModLoaded(String modid) {
        return ModList.get() != null && ModList.get().isLoaded(modid);
    }
}
