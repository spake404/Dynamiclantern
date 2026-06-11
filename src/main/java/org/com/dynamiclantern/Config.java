package org.com.dynamiclantern;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public final class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue RENDER_WAIST_LANTERN = BUILDER
            .comment("Render equipped Curios lanterns on the player's waist.")
            .define("renderWaistLantern", true);
    public static final ForgeConfigSpec.BooleanValue ENABLE_PHYSICS = BUILDER
            .comment("Apply a small pendulum swing to waist lantern rendering.")
            .define("enablePhysics", true);
    public static final ForgeConfigSpec.DoubleValue BOUNCINESS = BUILDER
            .comment("How strongly player movement affects lantern swing.")
            .defineInRange("bounciness", 0.75D, 0.0D, 3.0D);
    public static final ForgeConfigSpec.BooleanValue LEFT_SIDE = BUILDER
            .comment("Render the lantern on the left side of the waist.")
            .define("leftSide", false);
    public static final ForgeConfigSpec.BooleanValue BACK_LANTERN = BUILDER
            .comment("Render the lantern closer to the back of the waist.")
            .define("backLantern", false);
    public static final ForgeConfigSpec.BooleanValue SHADER_OFFHAND_OVERRIDE = BUILDER
            .comment("Make Iris/Oculus see a Curios lantern as the offhand item for shader held-item lighting.")
            .define("shaderOffhandOverride", true);
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> WAIST_RENDERABLE_ITEMS = BUILDER
            .comment("Item ids that Dynamic Lantern is allowed to render on the Curios belt slot.")
            .defineList("waistRenderableItems", WaistItemRules.DEFAULT_ITEM_IDS, value -> value instanceof String);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private Config() {
    }
}
