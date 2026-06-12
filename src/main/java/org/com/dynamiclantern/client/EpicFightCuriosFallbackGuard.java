package org.com.dynamiclantern.client;

import net.neoforged.fml.ModList;
import org.com.dynamiclantern.Diagnostics;

public final class EpicFightCuriosFallbackGuard {
    private static final String DYNAMIC_LANTERN_EPIC_FIGHT_LAYER = "org.com.dynamiclantern.client.EpicFightWaistItemLayer";
    private static final String EPIC_FIGHT_COMPAT_PACKAGE = "yesman.epicfight.compat.";
    private static final String EPIC_FIGHT_CURIOS_LAYER_SUFFIX = "CuriosCompat$PatchedCuriosLayerRenderer";
    private static final String EPIC_FIGHT_CURIOS_COMPAT_LAYER = "com.oneworldstudio.epicfightcurioscompat.ClientCuriosCompat$PatchedCuriosLayerRenderer";
    private static final boolean EPIC_FIGHT_LOADED = isModLoaded("epicfight");
    private static final boolean EPIC_FIGHT_CURIOS_COMPAT_LOADED = isModLoaded("epicfight_curios_compat");

    private EpicFightCuriosFallbackGuard() {
    }

    public static boolean isSuppressedLayerCall() {
        if (!EPIC_FIGHT_LOADED && !EPIC_FIGHT_CURIOS_COMPAT_LOADED) {
            return false;
        }

        LayerState state = inspectStack();
        boolean suppressed = state.fallbackLayer && !state.dynamicLanternLayer;
        if (state.fallbackLayer || state.dynamicLanternLayer) {
            Diagnostics.log(
                    "guard-" + state.fallbackClass + "-" + state.dynamicLanternLayer,
                    "guard state fallbackLayer={}, dynamicLanternLayer={}, suppressed={}, fallbackClass={}",
                    state.fallbackLayer,
                    state.dynamicLanternLayer,
                    suppressed,
                    state.fallbackClass);
        }
        return suppressed;
    }

    public static String currentCallSummary() {
        LayerState state = inspectStack();
        return "fallbackLayer=" + state.fallbackLayer
                + ",dynamicLanternLayer=" + state.dynamicLanternLayer
                + ",fallbackClass=" + state.fallbackClass;
    }

    private static LayerState inspectStack() {
        boolean fallbackLayer = false;
        boolean dynamicLanternLayer = false;
        String fallbackClass = "none";
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            String className = element.getClassName();
            if ((EPIC_FIGHT_LOADED && isEpicFightCuriosLayer(className))
                    || (EPIC_FIGHT_CURIOS_COMPAT_LOADED && EPIC_FIGHT_CURIOS_COMPAT_LAYER.equals(className))) {
                fallbackLayer = true;
                fallbackClass = className;
            } else if (DYNAMIC_LANTERN_EPIC_FIGHT_LAYER.equals(className)) {
                dynamicLanternLayer = true;
            }
        }

        return new LayerState(fallbackLayer, dynamicLanternLayer, fallbackClass);
    }

    private static boolean isEpicFightCuriosLayer(String className) {
        return className.startsWith(EPIC_FIGHT_COMPAT_PACKAGE)
                && className.endsWith(EPIC_FIGHT_CURIOS_LAYER_SUFFIX);
    }

    private static boolean isModLoaded(String modid) {
        return ModList.get() != null && ModList.get().isLoaded(modid);
    }

    private record LayerState(boolean fallbackLayer, boolean dynamicLanternLayer, String fallbackClass) {
    }
}
