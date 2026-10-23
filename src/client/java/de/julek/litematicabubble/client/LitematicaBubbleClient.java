package de.julek.litematicabubble.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class LitematicaBubbleClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Bubble.load();
        ClientTickEvents.END_CLIENT_TICK.register(client -> Bubble.tick());
    }
}
