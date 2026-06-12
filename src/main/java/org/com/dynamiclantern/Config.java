package org.com.dynamiclantern;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue RENDER_WAIST_LANTERN = BUILDER
            .comment("Render equipped Curios lanterns on the player's waist.")
            .define("renderWaistLantern", true);
    public static final ModConfigSpec.BooleanValue ENABLE_PHYSICS = BUILDER
            .comment("Apply a small pendulum swing to waist lantern rendering.")
            .define("enablePhysics", true);
    public static final ModConfigSpec.DoubleValue BOUNCINESS = BUILDER
            .comment("How strongly player movement affects lantern swing.")
            .defineInRange("bounciness", 0.75D, 0.0D, 3.0D);
    public static final ModConfigSpec.BooleanValue LEFT_SIDE = BUILDER
            .comment("Render the lantern on the left side of the waist.")
            .define("leftSide", false);
    public static final ModConfigSpec.BooleanValue BACK_LANTERN = BUILDER
            .comment("Render the lantern closer to the back of the waist.")
            .define("backLantern", false);
    public static final ModConfigSpec.BooleanValue SHADER_OFFHAND_OVERRIDE = BUILDER
            .comment("Make Iris/Oculus see a Curios belt light-emitting block item as the offhand item for shader held-item lighting.")
            .define("shaderOffhandOverride", true);
    public static final ModConfigSpec.BooleanValue SOULSPRING_LAMP_DEBUG_LOG = BUILDER
            .comment("Log Cold Sweat Soulspring Lamp Curios compatibility diagnostics while the lamp is equipped.")
            .define("soulspringLampDebugLog", false);
    public static final ModConfigSpec.ConfigValue<List<? extends String>> WAIST_RENDERABLE_ITEMS = BUILDER
            .comment("Additional item ids that Dynamic Lantern is allowed to render on the Curios belt slot.")
            .defineList("waistRenderableItems", List.of(), value -> value instanceof String);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
    }
}
