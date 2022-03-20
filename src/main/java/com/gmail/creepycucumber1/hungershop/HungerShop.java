package com.gmail.creepycucumber1.hungershop;

import com.gmail.creepycucumber1.hungershop.command.ConfigCommand;
import com.gmail.creepycucumber1.hungershop.files.Data;
import com.gmail.creepycucumber1.hungershop.files.Join;
import com.gmail.creepycucumber1.hungershop.files.Prices;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class HungerShop extends JavaPlugin {

    private static HungerShop plugin;

    public NPCManager npcManager;

    public static HungerShop getInstance() {
        return plugin;
    }

    private void setInstance(HungerShop instance) {
        HungerShop.plugin = instance;
    }

    FileConfiguration config = getConfig();

    public static Economy vault;

    public static World overworld;

    @Override
    public void onEnable() {
        //WORLD
        Bukkit.getWorlds().forEach(world -> {
            if(!world.toString().toLowerCase().contains("nether") && !world.toString().toLowerCase().contains("end"))
                overworld = world;
        });
        //NPC
        setInstance(this);
        this.npcManager = new NPCManager();
        this.npcManager.createNPC();
        Bukkit.getLogger().info("NEW NPC CREATED by HungerShop");
        //ECONOMY
        if(getServer().getPluginManager().getPlugin("Vault") != null){
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if(rsp != null){
                vault = rsp.getProvider();
            }
        }
        if(vault==null){
            getLogger().warning("Vault Economy not found by HungerShop!");
        }
        //CONFIG
        this.config.addDefault("Texture", "ewogICJ0aW1lc3RhbXAiIDogMTYyODg4MDkwNTE2NSwKICAicHJvZmlsZUlkIiA6ICI2MGU4YzNhMTdhOWY0ODA0ODRkNGU4YTk4MjZhMjc4MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJCZWFyIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzhmYjM3Y2E5OTZjMzVhMWUyZDM2ZjQzZDZlZjllMDk0YTA3NzU5ZjY5MmQ5N2M3OWJkZTVlMGFjOTcxNGMyN2YiCiAgICB9CiAgfQp9");
        this.config.addDefault("Signature", "W3mULHRrtO191UcemY4i21fnZGDtIlZqUSM9spidgSkbFjq5bn0nk7hQYkaMR1CYm8WJtDIx45k7AVbuE1qLJH12m2hfCa67QCdlMQPug3Cb6wtdbKfH3uyXWyvYIGiu+SdsH9XqAlrnuqrtVfzeAPscH+dZiM/zzzSzRTpRnsvBpnZsIah8VuIGmNOhlQcfa/2JouyZ5jjLTyv6VZ0QDRFvYrWtIMa4YCQXVjteTgGWLsYa5B5x81o4MD69Xecx40U68EynUOUh5WaPH+bv0JLvTIm1bBXHUtNsWclBZddMBq6IF0EjsfJwsquNKZ2DyMaor2SsCNmoeaWMWttQKXunXL0p/GwUf3Mmymfn2ODixBsY9TxX6zXGY6rb88wIEiXAxEXmL55nyAv1XzlfutandwayyM8CzHUPhvjtJup3+yRgMmDRc+z6EY7nKqfQA8YGXbIoHhLZamoQ3L4ekXkBdAmJ9olEXLyKLRmD0/ooIDOLFrnwG/C+s5z89pRZaK/m6Aa5B/VFM/KPmjPaJA88Hj41H2a0YiBSK6CchGN0arhnrSp8CLAKFqECaRQrGNOmMSR/on0bGh2usHNHnC1b2J5C1QksXTC9RvmRpk8r5JVLxaIXvW3L7TMxqDeBkryYqeSwQUUHkEhjgLxipzxkm+F7QxcbIAJVdJ4RZmQ=");
        this.config.addDefault("x", 202);
        this.config.addDefault("y", 66);
        this.config.addDefault("z", 118);
        this.config.options().copyDefaults(true);
        saveConfig();
        Data.setup();
        Data.save();
        Join.setup();
        Join.save();
        Prices.setup();
        Prices.save();
        //REPEATING TASK
        onGetClose();
        //BASIC
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new EventManager(this), this);
        getCommand("hungershop").setExecutor(new ConfigCommand());

        getLogger().info("HungerShop has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("HungerShop has been disabled!");
    }

    public Economy getVault() {
        return vault;
    }

    public void onGetClose() {

        Bukkit.getScheduler().scheduleSyncRepeatingTask(HungerShop.getInstance(), new Runnable() {

            HashMap<UUID, Location> map = new HashMap<>(); //Player UUID, location
            HashMap<UUID, World> map2 = new HashMap<>(); //Player UUID, world

            public void run() {
                for(Player p : Bukkit.getOnlinePlayers()) {
                    //LOCATION CHECK
                    Location loc = p.getLocation();
                    Location npcloc = new Location(overworld, getConfig().getInt("x"), getConfig().getInt("y"), getConfig().getInt("z"));

                    if(map.containsKey(p.getUniqueId()) && p.getWorld().equals(overworld) &&
                            map2.get(p.getUniqueId()).equals(overworld)) {
                        Location oldloc = map.get(p.getUniqueId());
                        if(oldloc.distance(npcloc) > loc.distance(npcloc) && loc.distance(npcloc) < 50 && oldloc.distance(npcloc) > 50) { //getting closer to npc in overworld, within 100 blocks
                            NPCManager.addJoinPacket(p);
                        }
                    }

                    //WORLD CHECK
                    World wld = p.getWorld();

                    if(map2.containsKey(p.getUniqueId())) {
                        World oldwld = map2.get(p.getUniqueId());
                        if(!oldwld.equals(overworld) && wld.equals(overworld)) { //going from end or nether to overworld
                            NPCManager.addJoinPacket(p);
                        }
                    }

                    //PUT INFO INTO MAPS
                    map.put(p.getUniqueId(), p.getLocation());
                    map2.put(p.getUniqueId(), p.getWorld());

                }
            }

        }, 0, 100);

    }

}
