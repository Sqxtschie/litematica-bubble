package de.julek.litematicabubble.client;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.util.SchematicWorldRefresher;
import fi.dy.masa.malilib.util.EntityUtils;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.LayerMode;
import fi.dy.masa.malilib.util.IntBoundingBox;
import fi.dy.masa.malilib.util.LayerRange;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * State of the "Bubble" layer mode: a sphere around the camera entity that follows it.
 */
public final class Bubble {
    public static final int MIN_RADIUS = 1;
    public static final int MAX_RADIUS = 64;
    /** Block size in percent with "Smaller Blocks" on. */
    public static final int MIN_BLOCK_SIZE = 10;
    public static final int MAX_BLOCK_SIZE = 90;
    private static final Path CONFIG = FabricLoader.getInstance().getConfigDir().resolve("litematica_bubble.txt");

    /** Set by LayerModeMixin while LayerMode initializes. */
    public static LayerMode MODE;

    private static volatile int radius = 10;
    private static boolean frozen;
    // Read from chunk builder threads
    private static volatile boolean smallBlocks;
    private static volatile int blockSize = 60;
    // Read from chunk builder threads, written from the client thread
    private static volatile int cx, cy, cz;
    // Optional world height limits, like All Below / All Above; MIN/MAX_VALUE = no limit
    private static volatile int minY = Integer.MIN_VALUE, maxY = Integer.MAX_VALUE;

    private Bubble() {
    }

    public static boolean isActive(LayerRange range) {
        return MODE != null && range.getLayerMode() == MODE;
    }

    public static int getRadius() {
        return radius;
    }

    public static boolean contains(int x, int y, int z) {
        long dx = x - cx, dy = y - cy, dz = z - cz;
        double r = radius + 0.5;
        return y >= minY && y <= maxY && dx * dx + dy * dy + dz * dz <= r * r;
    }

    public static int center(int axisOrdinal) {
        return axisOrdinal == 0 ? cx : axisOrdinal == 1 ? cy : cz;
    }

    /** Intersection of the given box with the bubble's bounding cube, or null. */
    public static IntBoundingBox clamp(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        int r = radius;
        minX = Math.max(minX, cx - r); maxX = Math.min(maxX, cx + r);
        minY = Math.max(minY, Math.max(cy - r, Bubble.minY)); maxY = Math.min(maxY, Math.min(cy + r, Bubble.maxY));
        minZ = Math.max(minZ, cz - r); maxZ = Math.min(maxZ, cz + r);
        return minX > maxX || minY > maxY || minZ > maxZ ? null : IntBoundingBox.createProper(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static void setRadius(int value) {
        int old = radius;
        radius = Math.clamp(value, MIN_RADIUS, MAX_RADIUS);
        if (radius != old) {
            markForRebuild(cx, cz, cx, cz, Math.max(old, radius));
            save();
        }
    }

    public static void changeRadius(int amount) {
        setRadius(radius + amount);
        InfoUtils.printActionbarMessage("litematica_bubble.message.radius", radius);
    }

    public static int getMinY() {
        return minY;
    }

    public static int getMaxY() {
        return maxY;
    }

    public static boolean hasHeightLimit() {
        return minY != Integer.MIN_VALUE || maxY != Integer.MAX_VALUE;
    }

    /** Moves the upper or lower height limit; an unset limit starts at the bubble's center. */
    public static void moveHeightLimit(boolean upper, int amount) {
        if (upper) {
            maxY = Math.max((maxY == Integer.MAX_VALUE ? cy : maxY) + amount, minY);
            InfoUtils.printActionbarMessage("litematica_bubble.message.max_y", maxY);
        } else {
            minY = Math.min((minY == Integer.MIN_VALUE ? cy : minY) + amount, maxY);
            InfoUtils.printActionbarMessage("litematica_bubble.message.min_y", minY);
        }
        markForRebuild(cx, cz, cx, cz, radius);
        save();
    }

    public static void clearHeightLimits() {
        minY = Integer.MIN_VALUE;
        maxY = Integer.MAX_VALUE;
        InfoUtils.printActionbarMessage("litematica_bubble.message.height_cleared");
        markForRebuild(cx, cz, cx, cz, radius);
        save();
    }

    public static boolean isFrozen() {
        return frozen;
    }

    public static void toggleFrozen() {
        frozen = !frozen;
        InfoUtils.printActionbarMessage(frozen ? "litematica_bubble.message.frozen" : "litematica_bubble.message.unfrozen");
    }

    public static boolean isSmallBlocks() {
        return smallBlocks;
    }

    public static void toggleSmallBlocks() {
        smallBlocks = !smallBlocks;
        if (smallBlocks) {
            InfoUtils.printActionbarMessage("litematica_bubble.message.block_size", blockSize);
        } else {
            InfoUtils.printActionbarMessage("litematica_bubble.message.small_blocks_off");
        }
        markForRebuild(cx, cz, cx, cz, radius);
        save();
    }

    /** Smaller Blocks is on and the Bubble mode is active. */
    public static boolean isSmallBlocksActive() {
        return smallBlocks && isActive(DataManager.getRenderLayerRange());
    }

    public static int getBlockSize() {
        return blockSize;
    }

    /** Changes the block size; also switches Smaller Blocks on, since that's the only way to see the change. */
    public static void changeBlockSize(int amount) {
        blockSize = Math.clamp(blockSize + amount, MIN_BLOCK_SIZE, MAX_BLOCK_SIZE);
        smallBlocks = true;
        InfoUtils.printActionbarMessage("litematica_bubble.message.block_size", blockSize);
        markForRebuild(cx, cz, cx, cz, radius);
        save();
    }

    /** Called every client tick: moves the bubble with the camera entity unless it's frozen. */
    static void tick() {
        Entity entity = EntityUtils.getCameraEntity();
        if (frozen || entity == null || !isActive(DataManager.getRenderLayerRange())) {
            return;
        }
        BlockPos pos = BlockPos.containing(entity.getEyePosition());
        if (pos.getX() == cx && pos.getY() == cy && pos.getZ() == cz) {
            return;
        }
        int oldX = cx, oldZ = cz;
        cx = pos.getX();
        cy = pos.getY();
        cz = pos.getZ();
        markForRebuild(oldX, oldZ, cx, cz, radius);
    }

    // Rebuilds the chunk columns covering the old and the new bubble
    private static void markForRebuild(int x1, int z1, int x2, int z2, int r) {
        DataManager.getSchematicPlacementManager().setVisibleSubChunksNeedsUpdate();
        for (int x = (Math.min(x1, x2) - r) >> 4; x <= (Math.max(x1, x2) + r) >> 4; x++) {
            for (int z = (Math.min(z1, z2) - r) >> 4; z <= (Math.max(z1, z2) + r) >> 4; z++) {
                SchematicWorldRefresher.INSTANCE.markSchematicChunksForRenderUpdate(x, z);
            }
        }
    }

    static void load() {
        try {
            if (Files.exists(CONFIG)) {
                // "radius [minY maxY [smallBlocks [blockSize]]]"; older versions stored less
                String[] parts = Files.readString(CONFIG).trim().split(" +");
                radius = Math.clamp(Integer.parseInt(parts[0]), MIN_RADIUS, MAX_RADIUS);
                if (parts.length >= 3) {
                    minY = Integer.parseInt(parts[1]);
                    maxY = Integer.parseInt(parts[2]);
                }
                if (parts.length >= 4) {
                    smallBlocks = Boolean.parseBoolean(parts[3]);
                }
                if (parts.length >= 5) {
                    blockSize = Math.clamp(Integer.parseInt(parts[4]), MIN_BLOCK_SIZE, MAX_BLOCK_SIZE);
                }
            }
        } catch (Exception e) {
            // Broken file: keep the defaults
        }
    }

    private static void save() {
        try {
            Files.writeString(CONFIG, radius + " " + minY + " " + maxY + " " + smallBlocks + " " + blockSize);
        } catch (Exception e) {
            // Not critical, the settings just won't persist
        }
    }
}
