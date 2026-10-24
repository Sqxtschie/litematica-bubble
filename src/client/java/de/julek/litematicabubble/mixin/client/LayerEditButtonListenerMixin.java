package de.julek.litematicabubble.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.julek.litematicabubble.client.Bubble;
import fi.dy.masa.malilib.util.LayerRange;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** The axis is meaningless for a sphere, so in Bubble mode the axis button toggles Smaller Blocks instead. */
@Mixin(targets = "fi.dy.masa.malilib.gui.GuiRenderLayerEditBase$ButtonListenerLayerEdit", remap = false)
public abstract class LayerEditButtonListenerMixin {
    @WrapOperation(method = "actionPerformedWithButton", at = @At(value = "INVOKE",
            target = "Lfi/dy/masa/malilib/util/LayerRange;setAxis"))
    private void litematicaBubble$toggleSmallBlocks(LayerRange range, Direction.Axis axis, Operation<Void> original) {
        if (Bubble.isActive(range)) {
            Bubble.toggleSmallBlocks();
        } else {
            original.call(range, axis);
        }
    }
}
