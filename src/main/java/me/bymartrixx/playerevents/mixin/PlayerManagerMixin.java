package me.bymartrixx.playerevents.mixin;

import me.bymartrixx.playerevents.PlayerEvents;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;
import java.util.Arrays;


import java.util.function.Function;
import java.util.function.Predicate;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "broadcast(Lnet/minecraft/text/Text;"
            + "Ljava/util/function/Function;"
            + "Z)V",
            at = @At("HEAD"),
            cancellable = true)
    private void broadcastAlternative(final Text message,
                                      final Function<ServerPlayerEntity, Text> playerMessageFactory,
                                      final boolean overlay,
                                      final CallbackInfo ci) {
        logic(message, ci);
    }

    @Inject(method = "broadcast(Lnet/minecraft/network/message/SignedMessage;"
            + "Ljava/util/function/Predicate;"
            + "Lnet/minecraft/server/network/ServerPlayerEntity;"
            + "Lnet/minecraft/network/message/MessageType$Parameters;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void broadcastAlternative(final SignedMessage message,
                                      final Predicate<ServerPlayerEntity> shouldSendFiltered,
                                      final @Nullable ServerPlayerEntity sender,
                                      final MessageType.Parameters params,
                                      final CallbackInfo ci) {
        logic(message.getContent(), ci);
    }

    @Unique
    private void logic(final Text message, final CallbackInfo info) {
        // Регулярное выражение для сообщений о присоединении/выходе и смерти
        String msg = message.getString();
        // Список ключевых слов
        List<String> keywords = Arrays.asList("joined", "left");

        // Проверяем, содержит ли сообщение какое-либо из ключевых слов
        for (String keyword : keywords) {
            if (msg.contains(keyword)) {
                if (info.isCancellable()) {
                    PlayerEvents.LOGGER.log(Level.INFO, "Canceling message \"{}\"", message.getString());
                    info.cancel();
                } else {
                    PlayerEvents.LOGGER.log(Level.WARN, "BroadcastChatMessage \"{}\" not cancellable", message.getString());
                }
                return; // Если ключевое слово найдено, прекращаем обработку
            }
        }

        // Логирование сообщений, которые не содержат ключевых слов
        PlayerEvents.LOGGER.log(Level.INFO, "Message did not match: \"{}\"", msg);
    }
}