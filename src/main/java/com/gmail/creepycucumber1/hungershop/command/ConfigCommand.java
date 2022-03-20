package com.gmail.creepycucumber1.hungershop.command;

import com.gmail.creepycucumber1.hungershop.files.Prices;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ConfigCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("hungershop")) {
            if (!sender.hasPermission("hungercore.owner")) {
                sender.sendMessage(ChatColor.GREEN + "Find the admin shop at spawn!");
                return true;
            }

            if (args.length != 2) {
                sender.sendMessage(ChatColor.GRAY + "Example: /hungershop end_stone 100.5");
                return true;
            }

            String mat;
            double price;
            try {
                mat = String.valueOf(args[0]).toUpperCase();
                price = Double.parseDouble(args[1]);
            } catch (Exception e) {
                sender.sendMessage(ChatColor.GRAY + "Unrecognized arguments. Example: /hungershop end_stone 100.5");
                return true;
            }
            double before = Prices.get().getDouble(mat);

            sender.sendMessage("Set the price of " + mat + " to " + price + "/item. It was previously " + before + "/item.");
            Prices.get().set(mat, price);
            Prices.save();
        }

        return true;
    }

}
