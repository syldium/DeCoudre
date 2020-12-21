package me.syldium.thimble.common.util;

import me.syldium.thimble.common.ThimblePlugin;
import me.syldium.thimble.common.player.PlayerAudience;
import me.syldium.thimble.common.player.MessageKey;
import me.syldium.thimble.common.player.Player;
import me.syldium.thimble.common.player.media.TimedMedia;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PlayerMap<E extends Identity> extends HashMap<UUID, E> implements PlayerAudience, ForwardingAudience, Iterable<E> {

    private final ThimblePlugin plugin;
    private final TimedMedia media;

    public PlayerMap(@NotNull ThimblePlugin plugin) {
        this.plugin = plugin;
        this.media = TimedMedia.from(plugin.getMainConfig(), "global");
    }

    public boolean add(@NotNull E player) {
        return this.put(player.uuid(), player) == null;
    }

    public E get(@NotNull Player player) {
        return this.get(player.uuid());
    }

    public boolean remove(@NotNull UUID uuid) {
        boolean removed = this.remove((Object) uuid) != null;
        Player player = this.plugin.getPlayer(uuid);
        if (player != null) {
            this.media.hide(player);
        }
        return removed;
    }

    public @NotNull Set<UUID> uuidSet() {
        return this.keySet();
    }

    public @NotNull Set<E> playerSet() {
        return new HashSet<>(this.values());
    }

    public boolean contains(UUID uuid) {
        return this.containsKey(uuid);
    }

    public boolean contains(E identity) {
        return this.containsValue(identity);
    }

    public void sendMessage(@NotNull MessageKey messageKey, @NotNull Template... templates) {
        Component component = ThimblePlugin.PREFIX.append(this.plugin.getMessageService().formatMessage(messageKey, templates));
        for (Player player : this.audiences()) player.sendMessage(component);
    }

    public void sendMessage(@NotNull MessageKey messageKey, @NotNull Predicate<@NotNull E> predicate, @NotNull Template... templates) {
        Component component = ThimblePlugin.PREFIX.append(this.plugin.getMessageService().formatMessage(messageKey, templates));
        for (E identity : this) {
            if (!predicate.test(identity)) {
                continue;
            }

            Player player = this.plugin.getPlayer(identity.uuid());
            if (player != null) {
                player.sendMessage(component);
            }
        }
    }

    public void sendActionBar(@NotNull MessageKey messageKey, @NotNull Template... templates) {
        Component component = this.plugin.getMessageService().formatMessage(messageKey, templates);
        for (Player player : this.audiences()) player.sendActionBar(component);
    }

    @Override
    public @NotNull Iterable<Player> audiences() {
        List<Player> players = new LinkedList<>();
        for (E identity : this) {
            Player player = this.plugin.getPlayer(identity.uuid());
            if (player != null) {
                players.add(player);
            }
        }
        return players;
    }

    @Override
    public @NotNull Iterator<E> iterator() {
        return this.values().iterator();
    }

    public void progress(float progress, int time) {
        this.media.progress(this, progress, time);
    }

    public void progress(int ticks, int total) {
        this.media.progress(this, ticks, total);
    }

    public void hide() {
        this.media.hide(this);
    }

    @Override
    public void sendExperienceChange(float percent, int level) {
        for (PlayerAudience expHolder : this.audiences()) expHolder.sendExperienceChange(percent, level);
    }

    @Override
    public void sendRealExperience() {
        for (PlayerAudience expHolder : this.audiences()) expHolder.sendRealExperience();
    }

    public @NotNull Stream<E> stream() {
        return this.values().stream();
    }
}
