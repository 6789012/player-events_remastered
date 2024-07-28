package me.bymartrixx.playerevents.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.advancement.Advancement;

public interface PlayerAdvancementCallback {
    Event<PlayerAdvancementCallback> EVENT = EventFactory.createArrayBacked(PlayerAdvancementCallback.class, (listeners) -> (player, message, advancement) -> {
        for (PlayerAdvancementCallback listener : listeners) {
            listener.advancementGet(player, message, advancement);
        }
    });

    void advancementGet(ServerPlayerEntity player, Text message, Advancement advancement);
}
