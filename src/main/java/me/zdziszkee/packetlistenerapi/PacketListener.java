package me.zdziszkee.packetlistenerapi;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

import static me.zdziszkee.packetlistenerapi.PacketListenerManager.getChannelPipeLine;


public final class PacketListener {
    private final ChannelHandler channelHandler;
    private final String handlerName;

    public PacketListener(MessageToMessageDecoder<? extends Packet<?>> packetHandler) {
        this.channelHandler = packetHandler;
        this.handlerName = "ingoing_handler" + PacketListenerManager.getListenerCount();
    }

    public PacketListener(ChannelOutboundHandlerAdapter channelOutboundHandlerAdapter) {
        this.channelHandler = channelOutboundHandlerAdapter;
        this.handlerName = "outgoing_handler" + PacketListenerManager.getListenerCount();
    }

    final void inject(final Player player) {
        final ChannelPipeline channelPipeline = getChannelPipeLine(player);
        if (channelPipeline.get("InGoingPacketInjector") != null) {
            return;
        }
        if (channelHandler instanceof MessageToMessageDecoder) {
            channelPipeline.addAfter("decoder", handlerName, channelHandler);
        } else if (channelHandler instanceof ChannelOutboundHandlerAdapter) {
            if (channelPipeline.get("OutGoingPacketInjector") != null) {
                return;
            }
            channelPipeline.addBefore("packet_handler", handlerName, channelHandler);
        }
    }

    public String getHandlerName() {
        return handlerName;
    }
}
