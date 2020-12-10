package me.packetlistenerapi;


import io.netty.channel.*;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

import java.util.List;

import static me.packetlistenerapi.PacketListenerManager.getChannelPipeLine;

public abstract class PacketListener<T extends Packet<?>> {
private final T t;
private final PacketHandler<T> packetHandler;
   public PacketListener(T t, PacketHandler<T> packetHandler) {
      this.t = t;
      this.packetHandler = packetHandler;
   }

   protected T getT() {
      return t;
   }
 protected void inject(final Player player){
    final ChannelPipeline channelPipeline = getChannelPipeLine(player);
    if (channelPipeline.get("InGoingPacketInjector") != null) {
       return;
    }
    channelPipeline.addAfter("inGoingDecoder", "InGoingPacketInjector", new MessageToMessageDecoder<T>() {
       @Override
       protected void decode(final ChannelHandlerContext channelHandlerContext,final T packet,final List<Object> list) {
          list.add(packet);
          getPacketHandler().onPacketPlayIn(player,packet);
       }
    });
    if (channelPipeline.get("OutGoingPacketInjector") != null) {
       return;
    }
    channelPipeline.addBefore("outGoingDecoder", "OutGoingPacketInjector", new ChannelOutboundHandlerAdapter(){
       @Override
       public void write(final ChannelHandlerContext channelHandlerContext,final Object o,final ChannelPromise channelPromise) throws Exception {
          if(o.getClass() !=t.getClass())return;
          getPacketHandler().onPacketPlayOut(player,t,channelHandlerContext);
          super.write(channelHandlerContext, o, channelPromise);
       }
    });
 }
   protected PacketHandler<T> getPacketHandler() {
      return packetHandler;
   }
}
