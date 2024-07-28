package me.bymartrixx.playerevents;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import me.bymartrixx.playerevents.api.event.*;
import me.bymartrixx.playerevents.command.PlayerEventsCommand;
import me.bymartrixx.playerevents.config.PlayerEventsConfig;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class PlayerEvents implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();

    public static final String MOD_ID = "player_events";
    public static final PlayerEventsConfig CONFIG = new PlayerEventsConfig();
    public static LuckPerms luckPerms = null;

    @Override
    public void onInitializeServer() {
        try {
            PlayerEventsConfig.Manager.loadConfig();
        } catch (JsonSyntaxException e) {
            LOGGER.error("Invalid JSON syntax in the config file", e);
        }

        ServerLifecycleEvents.SERVER_STARTED.register(this::onEnable);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            PlayerEventsCommand.register(dispatcher);
            CONFIG.registerCustomCommands(dispatcher);
        });

        PlayerFirstDeathCallback.EVENT.register((player, source) -> CONFIG.doFirstDeathActions(player));

        PlayerDeathCallback.EVENT.register((player, source) -> CONFIG.doDeathActions(player));

        PlayerFirstJoinCallback.EVENT.register(CONFIG::doFirstJoinActions);

        PlayerJoinCallback.EVENT.register(CONFIG::doJoinActions);

        PlayerLeaveCallback.EVENT.register(CONFIG::doLeaveActions);

        PlayerKillEntityCallback.EVENT.register(CONFIG::doKillEntityActions);

        PlayerKillPlayerCallback.EVENT.register(CONFIG::doKillPlayerActions);
		
		PlayerFirstJoinCallback.EVENT.register(CONFIG::doFirstItemJoin);

        CommandExecutionCallback.EVENT.register(CONFIG::doCustomCommandsActions);

        PlayerAdvancementCallback.EVENT.register(CONFIG::doGetAdvancement);

    }

    public void onEnable(MinecraftServer server){
        luckPerms = LuckPermsProvider.get();
    }
}
