package de.julek.litematicabubble.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.julek.litematicabubble.client.Bubble;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.render.infohud.StatusInfoRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Shows "r = 10" instead of the meaningless axis name in the status HUD. */
@Mixin(value = StatusInfoRenderer.class, remap = false)
public abstract class StatusInfoRendererMixin {
    @ModifyExpressionValue(method = "getText", at = @At(value = "INVOKE", target = "Ljava/lang/String;toLowerCase()Ljava/lang/String;"))
    private String litematicaBubble$radiusLabel(String axisName) {
        return Bubble.isActive(DataManager.getRenderLayerRange()) ? "r" : axisName;
    }
}
