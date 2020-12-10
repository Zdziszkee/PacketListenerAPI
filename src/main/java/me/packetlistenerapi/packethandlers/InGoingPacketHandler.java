package me.packetlistenerapi.packethandlers;

import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

public interface InGoingPacketHandler extends PacketHandler{
    void onIngoingPacket(Packet<?> packet, Player source, boolean isCanceled);
}
