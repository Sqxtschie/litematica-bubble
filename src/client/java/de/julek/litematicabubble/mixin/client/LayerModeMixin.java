package de.julek.litematicabubble.mixin.client;

import de.julek.litematicabubble.client.Bubble;
import fi.dy.masa.malilib.util.LayerMode;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

/**
 * Appends the BUBBLE constant to MaLiLib's LayerMode enum. It's injected right after $VALUES is assigned,
 * so CODEC, INDEX_TO_VALUE and VALUES (built later in the static initializer) include it.
 */
@Mixin(LayerMode.class)
public abstract class LayerModeMixin {
    @Shadow @Final @Mutable
    private static LayerMode[] $VALUES;

    @Invoker("<init>")
    private static LayerMode litematicaBubble$create(String name, int ordinal, int index, String configString, String translationKey) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At(value = "FIELD", opcode = Opcodes.PUTSTATIC,
            target = "Lfi/dy/masa/malilib/util/LayerMode;$VALUES:[Lfi/dy/masa/malilib/util/LayerMode;", shift = At.Shift.AFTER))
    private static void litematicaBubble$addBubble(CallbackInfo ci) {
        int id = $VALUES.length;
        Bubble.MODE = litematicaBubble$create("BUBBLE", id, id, "bubble", "litematica_bubble.layer_mode.bubble");
        $VALUES = Arrays.copyOf($VALUES, id + 1);
        $VALUES[id] = Bubble.MODE;
    }
}
