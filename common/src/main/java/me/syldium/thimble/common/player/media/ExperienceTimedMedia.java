package me.syldium.thimble.common.player.media;

import me.syldium.thimble.common.player.PlayerAudience;
import org.jetbrains.annotations.NotNull;

final class ExperienceTimedMedia implements TimedMedia {

    @Override
    public void progress(@NotNull PlayerAudience audience, float progress, int time) {
        audience.sendExperienceChange(progress, time);
    }

    @Override
    public void hide(@NotNull PlayerAudience audience) {

    }
}
