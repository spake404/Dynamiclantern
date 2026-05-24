package org.com.dynamiclantern.client;

import java.lang.reflect.Field;

public final class IrisBridge {
    private static Field shadowRendererActive;
    private static boolean lookedUpShadowRenderer;

    private IrisBridge() {
    }

    public static boolean isRenderingShadows() {
        if (!lookedUpShadowRenderer) {
            lookedUpShadowRenderer = true;
            try {
                Class<?> shadowRenderer = Class.forName("net.irisshaders.iris.shadows.ShadowRenderer");
                shadowRendererActive = shadowRenderer.getField("ACTIVE");
            } catch (ReflectiveOperationException ignored) {
                shadowRendererActive = null;
            }
        }

        if (shadowRendererActive == null) {
            return false;
        }

        try {
            return shadowRendererActive.getBoolean(null);
        } catch (IllegalAccessException ignored) {
            return false;
        }
    }
}
