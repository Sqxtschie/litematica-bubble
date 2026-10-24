package de.julek.litematicabubble.mixin.client;

import de.julek.litematicabubble.client.Bubble;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.position.LayerRange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** In Bubble mode "Set here" toggles freezing and the axis button toggles Smaller Blocks, so they're labeled accordingly. */
@Mixin(targets = "fi.dy.masa.malilib.gui.GuiRenderLayerEditBase$ButtonListenerLayerEdit$Type", remap = false)
public abstract class LayerEditButtonTypeMixin {
    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void litematicaBubble$freezeLabel(LayerRange range, CallbackInfoReturnable<String> cir) {
        if (!Bubble.isActive(range)) {
            return;
        }
        String name = ((Enum<?>) (Object) this).name();
        if (name.equals("SET_HERE")) {
            cir.setReturnValue(StringUtils.translate(Bubble.isFrozen() ? "litematica_bubble.gui.button.unfreeze" : "litematica_bubble.gui.button.freeze"));
        } else if (name.equals("AXIS")) {
            cir.setReturnValue(Bubble.isSmallBlocks()
                    ? StringUtils.translate("litematica_bubble.gui.button.small_blocks_on", Bubble.getBlockSize())
                    : StringUtils.translate("litematica_bubble.gui.button.small_blocks_off"));
        }
    }
}
