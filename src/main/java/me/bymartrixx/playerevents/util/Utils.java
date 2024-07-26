package me.bymartrixx.playerevents.util;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.TextParserUtils;
import me.bymartrixx.playerevents.PlayerEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static Text parseAndReplace(String input, ServerPlayerEntity player, Map<String, ?> placeholderArgs) {
        Text parsed = TextParserUtils.formatText(input);
        //parsed = processText(parsed);
        Text parsed2 = Placeholders.parseText(parsed, PlaceholderContext.of(player));

        return PlaceholderReplacingUtil.replacePlaceholders(input, parsed2, placeholderArgs);
    }

    public static Text parseAndReplace(String input, ServerCommandSource source, Map<String, ?> placeholderArgs) {
        Text parsed = TextParserUtils.formatText(input);
        //parsed = processText(parsed);
        Text parsed2;
        if (source.getEntity() != null) {
            parsed2 = Placeholders.parseText(parsed, PlaceholderContext.of(source.getEntity()));
        } else {
            parsed2 = Placeholders.parseText(parsed, PlaceholderContext.of(source.getServer()));
        }

        return PlaceholderReplacingUtil.replacePlaceholders(input, parsed2, placeholderArgs);
    }

    public static void message(ServerPlayerEntity player, Text msg, boolean broadcast) {
        //PlayerEvents.LOGGER.info("Изначальное сообщение msg: " + msg);
        msg = processText(msg);

        if (!broadcast) {
            player.sendMessage(msg);
        } else {
            MinecraftServer server = player.getServer();
            if (server == null) {
                return; // Shouldn't happen
            }

            server.sendMessage(msg);
            for (ServerPlayerEntity player1 : server.getPlayerManager().getPlayerList()) {
                player1.sendMessage(msg);
            }
        }
    }

    // Метод разбивает Text на слова, проверяет их и возвращает новый Text с примененными стилями
    public static Text processText(Text input) {
        List<Text> words = new ArrayList<>();
        String[] splitText = input.getString().split(" ");

        boolean hasUrl = false;

        for (String word : splitText) {
            Style style = input.getStyle();
            PlayerEvents.LOGGER.info(style);
            if (word.startsWith("http://") || word.startsWith("https://")) {
                style = Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, word))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Нажмите чтобы открыть")))
                        .withFormatting(Formatting.UNDERLINE, Formatting.BLUE);
                hasUrl = true;
            }
            else
            {
                hasUrl = false;
            }

            words.add(Text.literal(word).setStyle(style));
        }

        Text result = Text.literal("");
        for (Text word : words) {
            result = ((MutableText) result).append(word).append(" ");
        }

        //PlayerEvents.LOGGER.info(words);

        if (hasUrl)
        {
            return result;
        }
        else{
        return input;}
    }


    public static String doubleToStr(double d) {
        String str = String.format("%.1f", d);
        return str.replace(',', '.');
    }

    public static Text doubleToText(double d) {
        return Text.literal(doubleToStr(d));
    }
}
