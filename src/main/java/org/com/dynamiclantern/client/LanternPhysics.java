package org.com.dynamiclantern.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.com.dynamiclantern.Config;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

final class LanternPhysics {
    private static final Map<UUID, State> STATES = new ConcurrentHashMap<>();

    private LanternPhysics() {
    }

    static Vec3 update(Player player, Vec3 anchor, float partialTicks) {
        if (!Config.ENABLE_PHYSICS.get() || Minecraft.getInstance().screen != null || IrisBridge.isRenderingShadows()) {
            return Vec3.ZERO;
        }

        State state = STATES.computeIfAbsent(player.getUUID(), ignored -> new State());
        if (!state.wasCrouching && player.isCrouching()) {
            state.xAngle = Mth.PI / 8.0F;
            state.zAngle = Mth.PI / 10.0F;
        }
        state.wasCrouching = player.isCrouching();

        Vec3 previous = state.lastAnchor == null ? anchor : state.lastAnchor;
        Vec3 motion = toPlayerSpace(anchor.subtract(previous), player.getForward());
        float dt = Math.max(0.05F, partialTicks) / 20.0F;
        float response = Config.BOUNCINESS.get().floatValue();

        state.zVelocity += -pendulumForce(state.zAngle, motion.z, motion.y, response) * dt;
        state.zVelocity = Mth.clamp(state.zVelocity * 0.98F, -3.0F, 3.0F);
        state.zAngle = Mth.clamp(state.zAngle + state.zVelocity * dt, -Mth.PI / 3.0F, Mth.PI / 3.0F);

        state.xVelocity += -pendulumForce(state.xAngle, motion.x, motion.y, response) * dt;
        state.xVelocity = Mth.clamp(state.xVelocity * 0.98F, -3.0F, 3.0F);
        state.xAngle = Mth.clamp(state.xAngle + state.xVelocity * dt, -Mth.PI / 3.0F, Mth.PI / 3.0F);

        state.lastAnchor = anchor;
        return new Vec3(clean(state.xAngle), 0.0D, clean(state.zAngle));
    }

    private static float pendulumForce(float angle, double horizontalMotion, double verticalMotion, float response) {
        float gravity = 9.81F * Mth.sin(angle);
        float horizontal = (float) horizontalMotion * response * -50.0F;
        float vertical = (float) verticalMotion * response * -20.0F;
        return gravity + horizontal + vertical;
    }

    private static float clean(float value) {
        return Math.abs(value) < 0.01F ? 0.0F : value;
    }

    private static Vec3 toPlayerSpace(Vec3 motion, Vec3 forwardVector) {
        Vec3 forward = forwardVector.normalize();
        Vec3 up = new Vec3(0.0D, 1.0D, 0.0D);
        Vec3 right = forward.cross(up).normalize();
        up = right.cross(forward).normalize();
        return new Vec3(motion.dot(right), motion.dot(up), motion.dot(forward));
    }

    private static final class State {
        private float xAngle;
        private float xVelocity;
        private float zAngle;
        private float zVelocity;
        private boolean wasCrouching;
        private Vec3 lastAnchor;
    }
}
