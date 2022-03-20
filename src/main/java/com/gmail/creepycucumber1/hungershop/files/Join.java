package com.gmail.creepycucumber1.hungershop.files;

import com.gmail.creepycucumber1.hungershop.HungerShop;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Join {
    private static File file;

    private static FileConfiguration customFile;

    public static void setup() {
        file = new File(HungerShop.getInstance().getDataFolder(), "join.yml");
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException iOException) {}
        customFile = (FileConfiguration) YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get() {
        return customFile;
    }

    public static void save() {
        try {
            customFile.save(file);
        } catch (IOException e) {
            System.out.println("Couldn't save HungerShop join file.");
        }
    }

    public static void reload() {
        customFile = (FileConfiguration)YamlConfiguration.loadConfiguration(file);
    }

}
