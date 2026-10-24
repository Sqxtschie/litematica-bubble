package de.julek.litematicabubble.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import de.julek.litematicabubble.client.Bubble;
import fi.dy.masa.litematica.config.Configs;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.render.RenderUtils;
import fi.dy.masa.litematica.render.schematic.ChunkRendererSchematicVbo;
import fi.dy.masa.litematica.util.OverlayType;
import fi.dy.masa.malilib.config.options.ConfigBooleanHotkeyed;
import fi.dy.masa.malilib.util.data.Color4f;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Litematica renders every block in the clamped (cube) box without a per-block range check,
 * so the blocks between the cube and the sphere are skipped here.
 * Targets are selected by name only: their descriptors contain Minecraft types, which aren't remapped with remap = false.
 */
@Mixin(value = ChunkRendererSchematicVbo.class, remap = false)
public abstract class ChunkRendererSchematicVboMixin {
    @Shadow
    protected Color4f overlayColor;

    @Shadow
    protected abstract BlockPos.MutableBlockPos getChunkRelativePosition(BlockPos pos);

    @Inject(method = "renderBlocksAndOverlay", at = @At("HEAD"), cancellable = true)
    private void litematicaBubble$skipOutside(CallbackInfo ci, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) PoseStack poseStack) {
        if (!Bubble.isActive(DataManager.getRenderLayerRange())) {
            return;
        }
        if (!Bubble.contains(pos.getX(), pos.getY(), pos.getZ())) {
            ci.cancel();
        } else if (Bubble.isSmallBlocks()) {
            // Smaller Blocks: the pose is already translated to the block and popped by the caller afterwards
            float s = Bubble.getBlockSize() / 100f;
            poseStack.translate(0.5f, 0.5f, 0.5f);
            poseStack.scale(s, s, s);
            poseStack.translate(-0.5f, -0.5f, -0.5f);
        }
    }

    // Smaller Blocks: the overlay (the colored sides and outlines) is drawn as a shrunken box,
    // so the model-shaped and the reduced (merged with neighbors) variants are switched off
    @WrapOperation(method = "renderOverlay", at = @At(value = "INVOKE",
            target = "Lfi/dy/masa/malilib/config/options/ConfigBooleanHotkeyed;getBooleanValue()Z"))
    private boolean litematicaBubble$boxOverlay(ConfigBooleanHotkeyed config, Operation<Boolean> original) {
        if (Bubble.isSmallBlocksActive() && (config == Configs.Visuals.OVERLAY_REDUCED_INNER_SIDES
                || config == Configs.Visuals.SCHEMATIC_OVERLAY_MODEL_SIDES || config == Configs.Visuals.SCHEMATIC_OVERLAY_MODEL_OUTLINE)) {
            return false;
        }
        return original.call(config);
    }

    @ModifyArg(method = "renderOverlay", at = @At(value = "INVOKE",
            target = "Lfi/dy/masa/litematica/render/RenderUtils;drawBlockBoxBatchedQuads"), index = 2)
    private double litematicaBubble$shrinkSides(double expand) {
        return Bubble.isSmallBlocksActive() ? overlayExpand() : expand;
    }

    @ModifyArg(method = "renderOverlay", at = @At(value = "INVOKE",
            target = "Lfi/dy/masa/litematica/render/RenderUtils;drawBlockBoundingBoxOutlinesBatchedDebugLines"), index = 2)
    private double litematicaBubble$shrinkOutline(double expand) {
        return Bubble.isSmallBlocksActive() ? overlayExpand() : expand;
    }

    // The reduced edges are joined with the neighbors' edges, so each block gets its own shrunken outline instead
    @WrapOperation(method = "renderOverlay", at = @At(value = "INVOKE",
            target = "Lfi/dy/masa/litematica/render/schematic/ChunkRendererSchematicVbo;renderOverlayReducedEdges"))
    private void litematicaBubble$boxOutline(ChunkRendererSchematicVbo self, BlockPos pos, OverlayType[][][] adjacent, OverlayType type,
                                             float lineWidth, BufferBuilder buffer, Operation<Void> original) {
        if (Bubble.isSmallBlocksActive()) {
            Color4f c = this.overlayColor;
            RenderUtils.drawBlockBoundingBoxOutlinesBatchedDebugLines(getChunkRelativePosition(pos), new Color4f(c.r, c.g, c.b, 1f), overlayExpand(), lineWidth, buffer);
        } else {
            original.call(self, pos, adjacent, type, lineWidth, buffer);
        }
    }

    // A negative expand shrinks the box on every side
    private static double overlayExpand() {
        return -(1 - Bubble.getBlockSize() / 100.0) / 2;
    }
}
