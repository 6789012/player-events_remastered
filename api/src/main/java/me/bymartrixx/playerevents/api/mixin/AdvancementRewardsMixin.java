package me.bymartrixx.playerevents.api.mixin;


import me.bymartrixx.playerevents.api.event.PlayerAdvancementCallback;
import me.bymartrixx.playerevents.api.event.PlayerFirstJoinCallback;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.advancement.AdvancementRewards;

import java.util.Set;

@Mixin(value = PlayerAdvancementTracker.class, priority = 999)
public abstract class AdvancementRewardsMixin {

    @Shadow
    @Final
    private PlayerManager playerManager;

    @Shadow
    @Final
    private Set<Advancement> progressUpdates;

    @Shadow
    public abstract AdvancementProgress getProgress(Advancement advancement);

    @Shadow
    protected abstract void onStatusUpdate(Advancement advancement);

    @Shadow
    private ServerPlayerEntity owner;

    @Shadow
    protected abstract void endTrackingCompleted(Advancement advancement);



    /**
     * @author Stiven53
     * @reason Change the advancement vanilla message
     */
    @Overwrite
    public boolean grantCriterion(Advancement advancement, String criterionName) {
        boolean bl = false;
        AdvancementProgress advancementProgress = getProgress(advancement);
        boolean bl2 = advancementProgress.isDone();
        if (advancementProgress.obtain(criterionName)) {
            endTrackingCompleted(advancement);
            progressUpdates.add(advancement);
            bl = true;
            if (!bl2 && advancementProgress.isDone()) {
                advancement.getRewards().apply(owner);
                if (advancement.getDisplay() != null && advancement.getDisplay().shouldAnnounceToChat() && this.owner.getWorld().getGameRules().getBoolean(GameRules.ANNOUNCE_ADVANCEMENTS)) {
                    //this.playerManager.broadcast(Text.translatable("chat.type.advancement." + advancement.getDisplay().getFrame().getId(), new Object[]{this.owner.getDisplayName(), advancement.toHoverableText()}), false);
                    PlayerAdvancementCallback.EVENT.invoker().advancementGet(this.owner.getCommandSource().getPlayer(), Text.translatable("chat.type.advancement." + advancement.getDisplay().getFrame().getId()), advancement);
                }
            }
        }

        if (!bl2 && advancementProgress.isDone()) {
            onStatusUpdate(advancement);
        }

        return bl;
    }
}
