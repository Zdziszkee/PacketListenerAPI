package me.zdziszkee.packetlistenerapi.packethandlers;


import io.netty.channel.ChannelHandlerContext;
import org.bukkit.entity.Player;

public interface OutGoingPacketHandler<T> extends PacketHandler<T>{
    void onOutgoingPacket(Object packet, Player target, ChannelHandlerContext channelHandlerContext, boolean isCanceled);

}
