package me.packetlistenerapi.packetlisteners;


import io.netty.channel.ChannelPipeline;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class PacketListenerManager implements Listener {


    private static final List<PacketListener<? extends Packet<?>>> packetListeners = new ArrayList<>();

    protected static ChannelPipeline getChannelPipeLine(final Player player) {
        return (((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline());
    }

    private void inject(final Player player) {
        for (PacketListener<? extends Packet<?>> packetListener : packetListeners) {
            packetListener.inject(player);
        }
    }

    public void addPacketListener(final PacketListener<? extends Packet<?>> packetListener) {
        packetListeners.add(packetListener);
        injectAllOnlinePlayers();
    }

    @EventHandler
    private void onPlayerJoin(final PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        inject(player);
    }

    @EventHandler
    private void onPlayerQuit(final PlayerQuitEvent e) {
        final Player player = e.getPlayer();
        eject(player);
    }

    private void injectAllOnlinePlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            inject(player);
        }
    }

    private void eject(final Player player) {
        ChannelPipeline channelPipeline = getChannelPipeLine(player);
        if (channelPipeline.get("InGoingPacketInjector") != null)
            channelPipeline.remove("InGoingPacketInjector");
        if (channelPipeline.get("OutGoingPacketInjector") != null)
            channelPipeline.remove("OutGoingPacketInjector");
    }

}
