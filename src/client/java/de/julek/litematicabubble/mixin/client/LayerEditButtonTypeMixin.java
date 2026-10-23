package de.julek.litematicabubble.mixin.client;

import de.julek.litematicabubble.client.Bubble;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.LayerRange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** In Bubble mode the "Set here" button toggles freezing, so it's labeled accordingly. */
@Mixin(targets = "fi.dy.masa.malilib.gui.GuiRenderLayerEditBase$ButtonListenerLayerEdit$Type", remap = false)
public abstract class LayerEditButtonTypeMixin {
    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void litematicaBubble$freezeLabel(LayerRange range, CallbackInfoReturnable<String> cir) {
        if (((Enum<?>) (Object) this).name().equals("SET_HERE") && Bubble.isActive(range)) {
            cir.setReturnValue(StringUtils.translate(Bubble.isFrozen() ? "litematica_bubble.gui.button.unfreeze" : "litematica_bubble.gui.button.freeze"));
        }
    }
}
