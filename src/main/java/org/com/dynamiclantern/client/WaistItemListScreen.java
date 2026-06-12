package org.com.dynamiclantern.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import org.com.dynamiclantern.Config;
import org.com.dynamiclantern.WaistItemCache;
import org.com.dynamiclantern.WaistItemRules;

import java.util.List;
import java.util.Optional;

public class WaistItemListScreen extends Screen {
    private final Screen parent;
    private EditBox itemIdInput;
    private Component status = Component.empty();
    private int statusColor = 0xFFFFFF;
    private int scrollOffset;

    public WaistItemListScreen(Screen parent) {
        super(Component.translatable("dynamiclantern.config.waist_items.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int center = this.width / 2;
        int inputY = 48;

        this.itemIdInput = new EditBox(this.font, center - 155, inputY, 220, 20, Component.translatable("dynamiclantern.config.waist_items.input"));
        this.itemIdInput.setMaxLength(128);
        this.addRenderableWidget(this.itemIdInput);
        this.setInitialFocus(this.itemIdInput);

        this.addRenderableWidget(Button.builder(Component.translatable("dynamiclantern.config.waist_items.add"), button -> addItem())
                .bounds(center + 70, inputY, 85, 20)
                .build());

        addItemRows(center, inputY + 34);

        int bottomY = this.height - 48;
        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> close())
                .bounds(center - 75, bottomY, 150, 20)
                .build());
    }

    private void addItemRows(int center, int startY) {
        List<String> ids = WaistItemRules.getConfiguredItemIds();
        int visibleRows = visibleRows();
        scrollOffset = Math.max(0, Math.min(scrollOffset, Math.max(0, ids.size() - visibleRows)));

        for (int row = 0; row < visibleRows && scrollOffset + row < ids.size(); row++) {
            String id = ids.get(scrollOffset + row);
            int y = startY + row * 24;
            this.addRenderableWidget(Button.builder(rowLabel(id), button -> this.itemIdInput.setValue(id))
                    .bounds(center - 155, y, 220, 20)
                    .build());
            this.addRenderableWidget(Button.builder(Component.translatable("dynamiclantern.config.waist_items.remove"), button -> removeItem(id))
                    .bounds(center + 70, y, 85, 20)
                    .build());
        }

        int navY = Math.min(this.height - 76, startY + visibleRows * 24 + 4);
        this.addRenderableWidget(Button.builder(Component.literal("<"), button -> {
                    scrollOffset = Math.max(0, scrollOffset - 1);
                    rebuild();
                })
                .bounds(center - 155, navY, 40, 20)
                .build());
        this.addRenderableWidget(Button.builder(Component.literal(">"), button -> {
                    scrollOffset = Math.min(Math.max(0, ids.size() - visibleRows), scrollOffset + 1);
                    rebuild();
                })
                .bounds(center - 110, navY, 40, 20)
                .build());
    }

    private void addItem() {
        Optional<Item> item = WaistItemRules.addConfiguredItem(this.itemIdInput.getValue());
        if (item.isEmpty()) {
            status = Component.translatable("dynamiclantern.config.waist_items.invalid");
            statusColor = 0xFF5555;
            return;
        }

        DynamiclanternClient.registerConfiguredRenderer(item.get());
        Config.SPEC.save();
        status = Component.translatable("dynamiclantern.config.waist_items.added", WaistItemRules.itemId(item.get()));
        statusColor = 0x55FF55;
        this.itemIdInput.setValue("");
        rebuild();
    }

    private void removeItem(String id) {
        if (WaistItemRules.removeConfiguredItem(id)) {
            Config.SPEC.save();
            WaistItemCache.clearAll();
            status = Component.translatable("dynamiclantern.config.waist_items.removed", id);
            statusColor = 0xFFFF55;
        }
        rebuild();
    }

    private Component rowLabel(String id) {
        int maxWidth = 210;
        String label = this.font.plainSubstrByWidth(id, maxWidth);
        if (label.length() < id.length()) {
            label = this.font.plainSubstrByWidth(id, maxWidth - this.font.width("...")) + "...";
        }
        return Component.literal(label);
    }

    private int visibleRows() {
        return Math.max(3, (this.height - 170) / 24);
    }

    private void rebuild() {
        this.clearWidgets();
        this.init();
    }

    private void close() {
        Config.SPEC.save();
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void onClose() {
        close();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 18, 0xFFFFFF);
        graphics.drawString(this.font, Component.translatable("dynamiclantern.config.waist_items.input"), this.width / 2 - 155, 36, 0xA0A0A0);
        graphics.drawCenteredString(this.font, status, this.width / 2, this.height - 24, statusColor);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
