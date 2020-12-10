package me.packetlistenerapi;


import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PacketListenerAPI {
    private final PacketListenerManager packetListenerManager = new PacketListenerManager();
    private final JavaPlugin javaPlugin;

    public PacketListenerAPI(final JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        registerEvents();
    }

    public void addPacketListener(final PacketListener<? extends Packet<?>> packetListener) {
        packetListenerManager.addPacketListener(packetListener);
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(packetListenerManager, javaPlugin);
    }
}
