package me.bymartrixx.playerevents.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import me.bymartrixx.playerevents.PlayerEvents;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class TestCommand {
    public static LiteralCommandNode<ServerCommandSource> getNode() {
        LiteralCommandNode<ServerCommandSource> testNode = literal("test").build();

        LiteralCommandNode<ServerCommandSource> joinNode = literal("join")
                .executes(context -> {
                    PlayerEvents.CONFIG.testJoinActions(context.getSource());
                    return 1;
                })
                .build();

        LiteralCommandNode<ServerCommandSource> leaveNode = literal("leave")
                .executes(context -> {
                    PlayerEvents.CONFIG.testLeaveActions(context.getSource());
                    return 1;
                })
                .build();

        LiteralCommandNode<ServerCommandSource> deathNode = literal("death")
                .executes(context -> {
                    PlayerEvents.CONFIG.testDeathActions(context.getSource());
                    return 1;
                })
                .build();

        LiteralCommandNode<ServerCommandSource> killPlayerNode = literal("kill_player")
                .executes(context -> {
                    PlayerEvents.CONFIG.testKillPlayerActions(context.getSource());
                    return 1;
                })
                .build();

        LiteralCommandNode<ServerCommandSource> killEntityNode = literal("kill_entity")
                .executes(context -> {
                    PlayerEvents.CONFIG.testKillEntityActions(context.getSource());
                    return 1;
                })
                .build();

        LiteralCommandNode<ServerCommandSource> firstJoinNode = literal("first_join")
                .executes(context -> {
                    PlayerEvents.CONFIG.testFirstJoinActions(context.getSource());
                    return 1;
                })
                .build();

        LiteralCommandNode<ServerCommandSource> customCommandsNode = literal("custom_commands")
                .executes(context -> {
                    PlayerEvents.CONFIG.testCustomCommandsActions(context.getSource());
                    return 1;
                })
                .build();

        LiteralCommandNode<ServerCommandSource> firstDeathNode = literal("first_death")
                .executes(context -> {
                    PlayerEvents.CONFIG.testFirstDeathActions(context.getSource());
                    return 1;
                })
                .build();

        LiteralCommandNode<ServerCommandSource> everyNode = literal("*")
                .executes(context -> {
                    PlayerEvents.CONFIG.testEveryActionGroup(context.getSource());
                    return 1;
                })
                .build();

        testNode.addChild(joinNode);
        testNode.addChild(leaveNode);
        testNode.addChild(deathNode);
        testNode.addChild(killPlayerNode);
        testNode.addChild(killEntityNode);
        testNode.addChild(firstJoinNode);
        testNode.addChild(customCommandsNode);
        testNode.addChild(firstDeathNode);
        testNode.addChild(everyNode);

        return testNode;
    }
}
