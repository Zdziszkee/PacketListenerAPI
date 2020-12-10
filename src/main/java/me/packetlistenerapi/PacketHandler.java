package me.packetlistenerapi;

import io.netty.channel.ChannelHandlerContext;
import org.bukkit.entity.Player;

public interface PacketHandler<T> {
    void onPacketPlayIn(final Player player, T t);

    void onPacketPlayOut(final Player player, T t, final ChannelHandlerContext channelHandlerContext);
}
