package org.com.dynamiclantern.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.com.dynamiclantern.Config;

public class DynamiclanternConfigScreen extends Screen {
    private final Screen parent;

    public DynamiclanternConfigScreen(Screen parent) {
        super(Component.translatable("dynamiclantern.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int center = this.width / 2;
        int y = 48;
        addToggle(center - 155, y, "dynamiclantern.config.render", Config.RENDER_WAIST_LANTERN.get(), Config.RENDER_WAIST_LANTERN::set);
        addToggle(center + 5, y, "dynamiclantern.config.physics", Config.ENABLE_PHYSICS.get(), Config.ENABLE_PHYSICS::set);
        y += 26;
        addToggle(center - 155, y, "dynamiclantern.config.shader", Config.SHADER_OFFHAND_OVERRIDE.get(), Config.SHADER_OFFHAND_OVERRIDE::set);
        addToggle(center + 5, y, "dynamiclantern.config.left_side", Config.LEFT_SIDE.get(), Config.LEFT_SIDE::set);
        y += 26;
        addToggle(center - 155, y, "dynamiclantern.config.back", Config.BACK_LANTERN.get(), Config.BACK_LANTERN::set);
        addBounciness(center + 5, y);
        y += 40;
        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> {
            Config.SPEC.save();
            this.minecraft.setScreen(this.parent);
        }).bounds(center - 75, y, 150, 20).build());
    }

    private void addToggle(int x, int y, String key, boolean initial, BooleanSetter setter) {
        this.addRenderableWidget(Button.builder(toggleText(key, initial), button -> {
            boolean next = !button.getMessage().getString().endsWith("ON");
            setter.set(next);
            button.setMessage(toggleText(key, next));
        }).bounds(x, y, 150, 20).build());
    }

    private void addBounciness(int x, int y) {
        this.addRenderableWidget(Button.builder(bouncinessText(), button -> {
            double next = Config.BOUNCINESS.get() + 0.25D;
            if (next > 3.0D) {
                next = 0.0D;
            }
            Config.BOUNCINESS.set(next);
            button.setMessage(bouncinessText());
        }).bounds(x, y, 150, 20).build());
    }

    private static Component toggleText(String key, boolean value) {
        return Component.translatable(key).append(": ").append(value ? "ON" : "OFF");
    }

    private static Component bouncinessText() {
        return Component.translatable("dynamiclantern.config.bounciness").append(": " + String.format("%.2f", Config.BOUNCINESS.get()));
    }

    @Override
    public void onClose() {
        Config.SPEC.save();
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 18, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private interface BooleanSetter {
        void set(boolean value);
    }
}
