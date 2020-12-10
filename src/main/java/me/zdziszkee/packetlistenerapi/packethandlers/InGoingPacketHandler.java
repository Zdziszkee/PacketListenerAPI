package me.zdziszkee.packetlistenerapi.packethandlers;

import org.bukkit.entity.Player;

public interface InGoingPacketHandler<T> extends PacketHandler<T>{
    void onIngoingPacket(T t, Player source, boolean isCanceled);
}
