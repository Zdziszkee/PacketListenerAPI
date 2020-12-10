package me.packetlistenerapi.packethandlers;


import io.netty.channel.ChannelHandlerContext;
import org.bukkit.entity.Player;

public interface OutGoingPacketHandler extends PacketHandler{
    void onOutgoingPacket(Object packet, Player target, ChannelHandlerContext channelHandlerContext, boolean isCanceled);

}
