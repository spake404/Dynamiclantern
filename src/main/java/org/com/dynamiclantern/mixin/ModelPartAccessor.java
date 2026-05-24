package org.com.dynamiclantern.mixin;

import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(ModelPart.class)
public interface ModelPartAccessor {
    @Accessor("cubes")
    List<ModelPart.Cube> dynamiclantern$getCubes();

    @Accessor("children")
    Map<String, ModelPart> dynamiclantern$getChildren();
}
