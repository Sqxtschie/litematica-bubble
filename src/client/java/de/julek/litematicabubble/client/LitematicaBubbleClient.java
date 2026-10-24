package de.julek.litematicabubble.client;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.hotkeys.IKeyboardInputHandler;
import fi.dy.masa.malilib.util.input.ScanCodes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;

public class LitematicaBubbleClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Bubble.load();
        ClientTickEvents.END_CLIENT_TICK.register(client -> Bubble.tick());
        // Shift + Page Up/Down: upper height limit, Ctrl: lower limit, both: remove the limits, Alt: block size.
        // Litematica's own Page Up/Down hotkeys don't fire with extra keys held, so there's no clash.
        InputEventHandler.getInputManager().registerKeyboardInputHandler(new IKeyboardInputHandler() {
            @Override
            public boolean onKeyInput(KeyEvent input, boolean eventKeyState) {
                int key = input.key();
                // key() is the scan code in 26.3
                boolean shift = input.hasShiftDown(), ctrl = input.hasControlDown(), alt = input.hasAltDown();
                if (!eventKeyState || (key != ScanCodes.SCAN_PAGE_UP && key != ScanCodes.SCAN_PAGE_DOWN) || !(shift || ctrl || alt)
                        || Minecraft.getInstance().gui.screen() != null || !Bubble.isActive(DataManager.getRenderLayerRange())) {
                    return false;
                }
                if (alt) {
                    Bubble.changeBlockSize(key == ScanCodes.SCAN_PAGE_UP ? 10 : -10);
                } else if (shift && ctrl) {
                    Bubble.clearHeightLimits();
                } else {
                    Bubble.moveHeightLimit(shift, key == ScanCodes.SCAN_PAGE_UP ? 1 : -1);
                }
                return true;
            }
        });
    }
}
