package de.julek.litematicabubble.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import de.julek.litematicabubble.client.Bubble;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.render.schematic.ChunkRendererSchematicVbo;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Litematica renders every block in the clamped (cube) box without a per-block range check,
 * so the blocks between the cube and the sphere are skipped here.
 */
@Mixin(value = ChunkRendererSchematicVbo.class, remap = false)
public abstract class ChunkRendererSchematicVboMixin {
    @Inject(method = "renderBlocksAndOverlay", at = @At("HEAD"), cancellable = true)
    private void litematicaBubble$skipOutside(CallbackInfo ci, @Local(argsOnly = true) BlockPos pos) {
        if (Bubble.isActive(DataManager.getRenderLayerRange()) && !Bubble.contains(pos.getX(), pos.getY(), pos.getZ())) {
            ci.cancel();
        }
    }
}
