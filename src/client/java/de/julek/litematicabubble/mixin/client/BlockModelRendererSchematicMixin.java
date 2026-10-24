package de.julek.litematicabubble.mixin.client;

import de.julek.litematicabubble.client.Bubble;
import fi.dy.masa.litematica.render.schematic.BlockModelRendererSchematic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Smaller Blocks leave gaps between neighbors, so faces hidden by a neighbor have to be rendered too. */
@Mixin(value = BlockModelRendererSchematic.class, remap = false)
public abstract class BlockModelRendererSchematicMixin {
    @Inject(method = "shouldRenderModelSide", at = @At("HEAD"), cancellable = true)
    private static void litematicaBubble$noCulling(CallbackInfoReturnable<Boolean> cir) {
        if (Bubble.isSmallBlocksActive()) {
            cir.setReturnValue(true);
        }
    }
}
