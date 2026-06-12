package org.com.dynamiclantern.client;

import net.neoforged.fml.ModList;

public final class EpicFightCuriosFallbackGuard {
    private static final String EPIC_FIGHT_COMPAT_PACKAGE = "yesman.epicfight.compat.";
    private static final String EPIC_FIGHT_CURIOS_LAYER_SUFFIX = "CuriosCompat$PatchedCuriosLayerRenderer";
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
            if ((EPIC_FIGHT_LOADED && isEpicFightCuriosLayer(className))
                    || (EPIC_FIGHT_CURIOS_COMPAT_LOADED && EPIC_FIGHT_CURIOS_COMPAT_LAYER.equals(className))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEpicFightCuriosLayer(String className) {
        return className.startsWith(EPIC_FIGHT_COMPAT_PACKAGE)
                && className.endsWith(EPIC_FIGHT_CURIOS_LAYER_SUFFIX);
    }

    private static boolean isModLoaded(String modid) {
        return ModList.get() != null && ModList.get().isLoaded(modid);
    }
}
