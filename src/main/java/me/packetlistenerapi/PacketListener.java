package me.packetlistenerapi;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.bukkit.entity.Player;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import static me.packetlistenerapi.PacketListenerManager.getChannelPipeLine;

public  final   class PacketListener<T> {
    private final PacketHandler<T> packetHandler;
    private final T t;
    private boolean isCanceled;
    public PacketListener(PacketHandler<T> packetHandler) {
        this.isCanceled = false;
        this.packetHandler = packetHandler;
        this.t = (T) findSuperClassParameterType(this);
    }
    private final Class<?> findSuperClassParameterType(Object instance) {
        Class<?> subClass = instance.getClass();
        while (subClass != subClass.getSuperclass()) {
            subClass = subClass.getSuperclass();
            if (subClass == null) throw new IllegalArgumentException();
        }
        ParameterizedType parameterizedType = (ParameterizedType) subClass.getGenericSuperclass();
        return (Class<?>) parameterizedType.getActualTypeArguments()[0];
    }

    protected final void inject(final Player player) {
        final ChannelPipeline channelPipeline = getChannelPipeLine(player);
        if (channelPipeline.get("InGoingPacketInjector") != null) {
            return;
        }
        channelPipeline.addAfter("inGoingDecoder", "InGoingPacketInjector", new MessageToMessageDecoder<T>() {
            @Override
            protected void decode(final ChannelHandlerContext channelHandlerContext, final T packet, final List<Object> list) {
                if (isCanceled) return;
                list.add(packet);
                getPacketHandler().onPacketPlayIn(player, packet);
            }
        });
        if (channelPipeline.get("OutGoingPacketInjector") != null) {
            return;
        }
        channelPipeline.addBefore("outGoingDecoder", "OutGoingPacketInjector", new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(final ChannelHandlerContext channelHandlerContext, final Object o, final ChannelPromise channelPromise) throws Exception {
                if (!isCanceled) {
                    if (o.getClass() != t.getClass()) return;
                    getPacketHandler().onPacketPlayOut(player, t, channelHandlerContext);
                }
                super.write(channelHandlerContext, o, channelPromise);
            }
        });
    }

    public final boolean isCanceled() {
        return isCanceled;
    }

    public final void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    protected final PacketHandler<T> getPacketHandler() {
        return packetHandler;
    }
}
