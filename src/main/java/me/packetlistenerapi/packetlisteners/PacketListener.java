package me.packetlistenerapi.packetlisteners;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToMessageDecoder;
import me.packetlistenerapi.packethandlers.InGoingPacketHandler;
import me.packetlistenerapi.packethandlers.OutGoingPacketHandler;
import me.packetlistenerapi.packethandlers.PacketHandler;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

import java.util.List;

import static me.packetlistenerapi.packetlisteners.PacketListenerManager.getChannelPipeLine;


public final class PacketListener<T extends Packet<?>> {
    private final PacketHandler<T> packetHandler;
    private boolean isCanceled;

    protected PacketListener(PacketHandler<T> packetHandler) {
        this.isCanceled = false;
        this.packetHandler = packetHandler;
    }

    final void  inject(final Player player) {
        final ChannelPipeline channelPipeline = getChannelPipeLine(player);
        if (channelPipeline.get("InGoingPacketInjector") != null) {
            return;
        }
        if (packetHandler instanceof InGoingPacketHandler) {

            channelPipeline.addAfter("inGoingDecoder", "InGoingPacketInjector", new MessageToMessageDecoder<T>() {
                @Override
                protected void decode(final ChannelHandlerContext channelHandlerContext, final T packet, final List<Object> list) {
                    if (isCanceled) return;
                    list.add(packet);
                    ((InGoingPacketHandler<T>) packetHandler).onIngoingPacket(packet, player, isCanceled);
                }
            });
        } else if (packetHandler instanceof OutGoingPacketHandler) {
            if (channelPipeline.get("OutGoingPacketInjector") != null) {
                return;
            }
            channelPipeline.addBefore("outGoingDecoder", "OutGoingPacketInjector", new ChannelOutboundHandlerAdapter() {
                @Override
                public void write(final ChannelHandlerContext channelHandlerContext, final Object o, final ChannelPromise channelPromise) throws Exception {
                    if (!isCanceled) {
                        ((OutGoingPacketHandler<T>) packetHandler).onOutgoingPacket(o, player, channelHandlerContext, isCanceled);

                        super.write(channelHandlerContext, o, channelPromise);
                    }
                }
            });
        }
    }

    public final boolean isCanceled() {
        return isCanceled;
    }

    public final void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

}
