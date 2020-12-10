package me.zdziszkee.packetlistenerapi;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

import static me.zdziszkee.packetlistenerapi.PacketListenerManager.getChannelPipeLine;


public final class PacketListener {
    private boolean isCanceled;
    private final ChannelHandler channelHandler;

    public PacketListener(MessageToMessageDecoder<? extends Packet<?>> packetHandler) {
        this.channelHandler = packetHandler;
        this.isCanceled = false;
    }

    public PacketListener(ChannelOutboundHandlerAdapter channelOutboundHandlerAdapter) {
        this.isCanceled = false;
        this.channelHandler = channelOutboundHandlerAdapter;
    }

    final void inject(final Player player) {
        final ChannelPipeline channelPipeline = getChannelPipeLine(player);
        if (channelPipeline.get("InGoingPacketInjector") != null) {
            return;
        }
        if (channelHandler instanceof MessageToMessageDecoder) {
            channelPipeline.addAfter("decoder", "InGoingPacketInjector", channelHandler);
        } else if (channelHandler instanceof ChannelOutboundHandlerAdapter) {
            if (channelPipeline.get("OutGoingPacketInjector") != null) {
                return;
            }
            channelPipeline.addBefore("packet_handler", "OutGoingPacketInjector", channelHandler);
        }
    }

    public final boolean isCanceled() {
        return isCanceled;
    }

    public final void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

}
