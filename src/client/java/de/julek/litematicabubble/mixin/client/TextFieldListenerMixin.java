package de.julek.litematicabubble.mixin.client;

import de.julek.litematicabubble.client.Bubble;
import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.util.LayerMode;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Lets the radius be typed into the render layer GUI's text field. */
@Mixin(targets = "fi.dy.masa.malilib.gui.GuiRenderLayerEditBase$TextFieldListener", remap = false)
public abstract class TextFieldListenerMixin {
    @Shadow @Final
    private LayerMode mode;

    @Inject(method = "onTextChange(Lfi/dy/masa/malilib/gui/GuiTextFieldGeneric;)Z", at = @At("HEAD"), cancellable = true)
    private void litematicaBubble$setRadius(GuiTextFieldGeneric textField, CallbackInfoReturnable<Boolean> cir) {
        if (this.mode == Bubble.MODE) {
            try {
                Bubble.setRadius(Integer.parseInt(textField.getValue()));
                cir.setReturnValue(true);
            } catch (NumberFormatException e) {
                cir.setReturnValue(false);
            }
        }
    }
}
