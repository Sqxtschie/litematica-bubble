package de.julek.litematicabubble.mixin.client;

import de.julek.litematicabubble.client.Bubble;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.IntBoundingBox;
import fi.dy.masa.malilib.util.LayerRange;
import fi.dy.masa.malilib.util.SubChunkPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Bubble behaviour for every LayerRange query. The vanilla modes only know a single axis,
 * so each query gets a Bubble branch; the exact sphere test lives in isPositionWithinRange.
 */
@Mixin(value = LayerRange.class, remap = false)
public abstract class LayerRangeMixin {
    @Shadow
    protected Direction.Axis axis;

    private boolean litematicaBubble$active() {
        return Bubble.isActive((LayerRange) (Object) this);
    }

    @Inject(method = "getLayerMin", at = @At("HEAD"), cancellable = true)
    private void litematicaBubble$min(CallbackInfoReturnable<Integer> cir) {
        if (litematicaBubble$active()) {
            int min = Bubble.center(this.axis.ordinal()) - Bubble.getRadius();
            cir.setReturnValue(this.axis == Direction.Axis.Y ? Math.max(min, Bubble.getMinY()) : min);
        }
    }

    @Inject(method = "getLayerMax", at = @At("HEAD"), cancellable = true)
    private void litematicaBubble$max(CallbackInfoReturnable<Integer> cir) {
        if (litematicaBubble$active()) {
            int max = Bubble.center(this.axis.ordinal()) + Bubble.getRadius();
            cir.setReturnValue(this.axis == Direction.Axis.Y ? Math.min(max, Bubble.getMaxY()) : max);
        }
    }

    @Inject(method = "getCurrentLayerValue", at = @At("HEAD"), cancellable = true)
    private void litematicaBubble$value(boolean second, CallbackInfoReturnable<Integer> cir) {
        if (litematicaBubble$active()) cir.setReturnValue(Bubble.getRadius());
    }

    @Inject(method = "getCurrentLayerString", at = @At("HEAD"), cancellable = true)
    private void litematicaBubble$string(CallbackInfoReturnable<String> cir) {
        if (litematicaBubble$active()) {
            String frozen = Bubble.isFrozen() ? " " + StringUtils.translate("litematica_bubble.hud.frozen") : "";
            String height = "";
            if (Bubble.hasHeightLimit()) {
                int min = Bubble.getMinY(), max = Bubble.getMaxY();
                height = ", Y " + (min == Integer.MIN_VALUE ? "-" : min) + ".." + (max == Integer.MAX_VALUE ? "-" : max);
            }
            cir.setReturnValue(Bubble.getRadius() + height + frozen);
        }
    }

    // The "layerSetHere" hotkey and the "Set here" button in the render layer GUI
    // Hooks the int overload (only called by the Entity one): its descriptor has no Minecraft types to remap
    @Inject(method = "setSingleBoundaryToPosition(I)V", at = @At("HEAD"), cancellable = true)
    private void litematicaBubble$freeze(int pos, CallbackInfo ci) {
        if (litematicaBubble$active()) {
            Bubble.toggleFrozen();
            ci.cancel();
        }
    }

    // Page Up / Page Down and the +/- button in the render layer GUI
    @Inject(method = "moveLayer", at = @At("HEAD"), cancellable = true)
    private void litematicaBubble$move(int amount, CallbackInfoReturnable<Boolean> cir) {
        if (litematicaBubble$active()) {
            Bubble.changeRadius(amount);
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isPositionWithinRange(III)Z", at = @At("HEAD"), cancellable = true)
    private void litematicaBubble$within(int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (litematicaBubble$active()) cir.setReturnValue(Bubble.contains(x, y, z));
    }

    // Render the faces on the bubble's surface, like Litematica does at a layer cut
    @Inject(method = "isPositionAtRenderEdgeOnSide", at = @At("HEAD"), cancellable = true)
    private void litematicaBubble$edge(BlockPos pos, Direction side, CallbackInfoReturnable<Boolean> cir) {
        if (litematicaBubble$active()) {
            BlockPos n = pos.relative(side);
            cir.setReturnValue(Bubble.contains(pos.getX(), pos.getY(), pos.getZ()) && !Bubble.contains(n.getX(), n.getY(), n.getZ()));
        }
    }

    @Inject(method = "intersects(Lfi/dy/masa/malilib/util/SubChunkPos;)Z", at = @At("HEAD"), cancellable = true)
    private void litematicaBubble$intersectsSection(SubChunkPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (litematicaBubble$active()) {
            int x = pos.getX() << 4, y = pos.getY() << 4, z = pos.getZ() << 4;
            cir.setReturnValue(Bubble.clamp(x, y, z, x + 15, y + 15, z + 15) != null);
        }
    }

    @Inject(method = "intersectsBox(IIIIII)Z", at = @At("HEAD"), cancellable = true)
    private void litematicaBubble$intersectsBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, CallbackInfoReturnable<Boolean> cir) {
        if (litematicaBubble$active()) cir.setReturnValue(Bubble.clamp(minX, minY, minZ, maxX, maxY, maxZ) != null);
    }

    @Inject(method = "getClampedArea(IIIIII)Lfi/dy/masa/malilib/util/IntBoundingBox;", at = @At("HEAD"), cancellable = true)
    private void litematicaBubble$clampedArea(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, CallbackInfoReturnable<IntBoundingBox> cir) {
        if (litematicaBubble$active()) cir.setReturnValue(Bubble.clamp(minX, minY, minZ, maxX, maxY, maxZ));
    }

    @Inject(method = "getClampedRenderBoundingBox", at = @At("HEAD"), cancellable = true)
    private void litematicaBubble$clampedRenderBox(IntBoundingBox box, CallbackInfoReturnable<IntBoundingBox> cir) {
        if (litematicaBubble$active()) cir.setReturnValue(Bubble.clamp(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ()));
    }

    @Inject(method = "getClampedValue", at = @At("HEAD"), cancellable = true)
    private void litematicaBubble$clampedValue(int value, Direction.Axis axis, CallbackInfoReturnable<Integer> cir) {
        if (litematicaBubble$active()) {
            int c = Bubble.center(axis.ordinal()), r = Bubble.getRadius();
            int min = c - r, max = c + r;
            if (axis == Direction.Axis.Y) {
                min = Math.max(min, Bubble.getMinY());
                max = Math.max(Math.min(max, Bubble.getMaxY()), min);
            }
            cir.setReturnValue(Math.clamp(value, min, max));
        }
    }

    @Inject(method = "getExpandedBox", at = @At("HEAD"), cancellable = true)
    private void litematicaBubble$expandedBox(Level world, int amount, CallbackInfoReturnable<IntBoundingBox> cir) {
        if (litematicaBubble$active()) {
            int r = Bubble.getRadius() + amount;
            int x = Bubble.center(0), y = Bubble.center(1), z = Bubble.center(2);
            cir.setReturnValue(IntBoundingBox.createProper(x - r, y - r, z - r, x + r, y + r, z + r));
        }
    }
}
