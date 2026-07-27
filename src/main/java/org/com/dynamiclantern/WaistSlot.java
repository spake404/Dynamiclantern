package org.com.dynamiclantern;

import top.theillusivec4.curios.api.SlotContext;

public record WaistSlot(
        Source source,
        String identifier,
        int index,
        boolean cosmetic,
        boolean visible) {

    public static WaistSlot fromCurios(SlotContext slotContext) {
        if (slotContext == null) {
            return null;
        }
        return new WaistSlot(
                Source.CURIOS,
                slotContext.identifier(),
                slotContext.index(),
                slotContext.cosmetic(),
                slotContext.visible());
    }

    public boolean matches(SlotContext slotContext) {
        return source == Source.CURIOS
                && slotContext != null
                && identifier.equals(slotContext.identifier())
                && index == slotContext.index()
                && cosmetic == slotContext.cosmetic()
                && visible == slotContext.visible();
    }

    public enum Source {
        CURIOS(0),
        ACCESSORIES(1);

        private final int priority;

        Source(int priority) {
            this.priority = priority;
        }

        public int priority() {
            return priority;
        }
    }
}
