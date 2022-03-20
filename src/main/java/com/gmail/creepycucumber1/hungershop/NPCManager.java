package com.gmail.creepycucumber1.hungershop;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPCManager {
    private HungerShop plugin = HungerShop.getInstance();

    private static List<EntityPlayer> NPC = new ArrayList<>();

    public static UUID randomUUID = UUID.randomUUID();
    //note - 1.18 sendPacket, addEntity (44), and setLocation (42) are all "a"

    public void createNPC() {
        DedicatedServer dedicatedServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld)HungerShop.overworld).getHandle();
        GameProfile gameProfile = new GameProfile(randomUUID, "shopkeeper");
        ChangeSkin(gameProfile);
        EntityPlayer npc = new EntityPlayer(dedicatedServer, world, gameProfile);
        npc.a(plugin.getConfig().getInt("x") + .5, plugin.getConfig().getInt("y") + .0, plugin.getConfig().getInt("z") + .5, 90.0F, 0.0F);
        new PlayerConnection(dedicatedServer, new NetworkManager(EnumProtocolDirection.a), npc);
        world.a(npc);
        addNPCPacket(npc);
        NPC.add(npc);
    }

    private void ChangeSkin(GameProfile profile) {
        String texture = this.plugin.getConfig().getString("Texture");
        String signature = this.plugin.getConfig().getString("Signature");
        profile.getProperties().put("textures", new Property("textures", texture, signature));
    }

    public static void addNPCPacket(EntityPlayer npc) {
        NPCManager nPCManager = new NPCManager();
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = (((CraftPlayer)player).getHandle()).b;
            connection.a(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, npc));
            connection.a(new PacketPlayOutNamedEntitySpawn(npc));
            connection.a(new PacketPlayOutEntityHeadRotation(npc, (byte) ((90.0F * 256.0F) / 360.0F)));
            for(int w = 1; w < 20; w++) { //repeatedly sends head rotation packet to client, makes sure head rotates
                Bukkit.getScheduler().runTaskLater(nPCManager.plugin, new Runnable() {
                    @Override
                    public void run() {
                        connection.a(new PacketPlayOutEntityHeadRotation(npc, (byte) ((90.0F * 256.0F) / 360.0F)));
                        connection.a(new PacketPlayOutEntity.PacketPlayOutEntityLook(0, (byte) ((90.0F * 256.0F) / 360.0F), (byte) ((0.0F * 256.0F) / 360.0F), true));
                    }
                }, w);
            }
            nPCManager.removeNPC(connection, npc);
        }
    }

    public static void addJoinPacket(Player player) {
        NPCManager nPCManager = new NPCManager();
        for (EntityPlayer npc : NPC) {
            PlayerConnection connection = (((CraftPlayer)player).getHandle()).b;
            connection.a(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, npc));
            connection.a(new PacketPlayOutNamedEntitySpawn(npc));
            connection.a(new PacketPlayOutEntityHeadRotation(npc, (byte) ((90.0F * 256.0F) / 360.0F)));
            for(int w = 1; w < 20; w++) { //repeatedly sends head rotation packet to client, makes sure head rotates
                Bukkit.getScheduler().runTaskLater(nPCManager.plugin, new Runnable() {
                    @Override
                    public void run() {
                        connection.a(new PacketPlayOutEntityHeadRotation(npc, (byte) ((90.0F * 256.0F) / 360.0F)));
                        connection.a(new PacketPlayOutEntity.PacketPlayOutEntityLook(0, (byte) ((90.0F * 256.0F) / 360.0F), (byte) ((0.0F * 256.0F) / 360.0F), true));
                    }
                }, w);
            }
            nPCManager.removeNPC(connection, npc);
        }
    }

    public static List<EntityPlayer> getNPCs() {
        return NPC;
    }

    public void removeNPC(final PlayerConnection connection, final EntityPlayer npc) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(HungerShop.getPlugin(HungerShop.class), new Runnable() {
            public void run() {
                connection.a((Packet)new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.e, npc));
            }
        }, 50);
    }
}
