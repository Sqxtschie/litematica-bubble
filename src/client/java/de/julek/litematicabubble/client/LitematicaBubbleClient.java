package de.julek.litematicabubble.client;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.hotkeys.IKeyboardInputHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;

public class LitematicaBubbleClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Bubble.load();
        ClientTickEvents.END_CLIENT_TICK.register(client -> Bubble.tick());
        // Shift + Page Up/Down: upper height limit, Ctrl: lower limit, both: remove the limits.
        // Litematica's own Page Up/Down hotkeys don't fire with extra keys held, so there's no clash.
        InputEventHandler.getInputManager().registerKeyboardInputHandler(new IKeyboardInputHandler() {
            @Override
            public boolean onKeyInput(KeyEvent input, boolean eventKeyState) {
                int key = input.key();
                boolean shift = input.hasShiftDown(), ctrl = input.hasControlDown();
                if (!eventKeyState || (key != GLFW.GLFW_KEY_PAGE_UP && key != GLFW.GLFW_KEY_PAGE_DOWN) || !(shift || ctrl)
                        || Minecraft.getInstance().screen != null || !Bubble.isActive(DataManager.getRenderLayerRange())) {
                    return false;
                }
                if (shift && ctrl) {
                    Bubble.clearHeightLimits();
                } else {
                    Bubble.moveHeightLimit(shift, key == GLFW.GLFW_KEY_PAGE_UP ? 1 : -1);
                }
                return true;
            }
        });
    }
}
