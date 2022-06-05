package com.gmail.creepycucumber1.hungershop;

import com.gmail.creepycucumber1.hungershop.files.Data;
import com.gmail.creepycucumber1.hungershop.files.Join;
import com.gmail.creepycucumber1.hungershop.files.Prices;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

public class EventManager implements Listener {

    private HungerShop plugin;

    public EventManager(HungerShop instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        if (!NPCManager.getNPCs().isEmpty()) {
            NPCManager.addJoinPacket(p, plugin);
        }

        ZoneId z = ZoneId.of("America/Chicago");
        LocalDate ld = LocalDate.now(z);
        int dayOfMonth = ld.getDayOfMonth();
        if(Join.get().getInt(String.valueOf(p.getUniqueId())) != dayOfMonth) { //player hasn't logged in yet today
            Join.get().set(String.valueOf(p.getUniqueId()), dayOfMonth); //logs player join for the day
            Join.save();
            Data.get().set(String.valueOf(p.getUniqueId()), 0); //resets iron limit
            Data.save();
        }
    }

    @EventHandler
    public void onClickShopkeeper(PlayerInteractAtEntityEvent e) {
        Player p = e.getPlayer();
        if (e.getRightClicked().getUniqueId().equals(NPCManager.randomUUID) && e
                .getHand().equals(EquipmentSlot.HAND)) {

            //create gui
            Inventory gui = Bukkit.createInventory(p, 27, ChatColor.BOLD + "Admin Shop");

            //create items
            ArrayList<ItemStack> items = new ArrayList<>();

            ItemStack netherite = new ItemStack(Material.NETHERITE_INGOT, 1);
            ItemStack lapis = new ItemStack(Material.LAPIS_LAZULI, 1);
            ItemStack coal = new ItemStack(Material.COAL, 1);
            ItemStack copper = new ItemStack(Material.COPPER_INGOT, 1);
            ItemStack iron = new ItemStack(Material.IRON_INGOT, 1);
            ItemStack rawiron = new ItemStack(Material.RAW_IRON, 1);
            ItemStack gold = new ItemStack(Material.GOLD_INGOT, 1);
            ItemStack rawgold = new ItemStack(Material.RAW_GOLD, 1);
            ItemStack emerald = new ItemStack(Material.EMERALD, 1);
            items.add(netherite); items.add(lapis); items.add(coal);
            items.add(copper); items.add(iron); items.add(rawiron);
            items.add(gold); items.add(rawgold); items.add(emerald);

            ItemStack diamond = new ItemStack(Material.DIAMOND, 1);
            ItemStack redstone = new ItemStack(Material.REDSTONE, 1);
            ItemStack obsidian = new ItemStack(Material.OBSIDIAN, 1);
            ItemStack netherrack = new ItemStack(Material.NETHERRACK, 1);
            ItemStack endstone = new ItemStack(Material.END_STONE, 1);
            ItemStack granite = new ItemStack(Material.GRANITE, 1);
            ItemStack andesite = new ItemStack(Material.ANDESITE, 1);
            ItemStack diorite = new ItemStack(Material.DIORITE, 1);
            ItemStack stone = new ItemStack(Material.STONE, 1);
            items.add(diamond); items.add(redstone); items.add(obsidian);
            items.add(netherrack); items.add(endstone); items.add(granite);
            items.add(andesite); items.add(diorite); items.add(stone);

            ItemStack cobblestone = new ItemStack(Material.COBBLESTONE, 1);
            ItemStack dirt = new ItemStack(Material.DIRT, 1);
            ItemStack magmablock = new ItemStack(Material.MAGMA_BLOCK, 1);
            ItemStack basalt = new ItemStack(Material.BASALT, 1);
            ItemStack soulsand = new ItemStack(Material.SOUL_SAND, 1);
            ItemStack quartzblock = new ItemStack(Material.QUARTZ_BLOCK, 1);
            items.add(cobblestone); items.add(dirt); items.add(magmablock);
            items.add(basalt); items.add(soulsand); items.add(quartzblock);

            //add lore/name, add to gui
            int i = 0;
            ItemStack[] menu_items = new ItemStack[items.size()];
            for(ItemStack stack : items) {
                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(ChatColor.BOLD + stack.getType().getKey().getKey().toUpperCase().replace("_", " "));
                ArrayList<String> lore = new ArrayList<>();
                lore.add(ChatColor.ITALIC + "$" + Prices.get().getDouble(stack.getType().getKey().getKey().toUpperCase()) + "/item");
                meta.setLore(lore);
                stack.setItemMeta(meta);

                menu_items[i++] = stack;
            }

            gui.setContents(menu_items);
            gui.setItem(26, info());

            p.openInventory(gui);

        }
    }

    public static final DecimalFormat decimalFormat = new DecimalFormat("##,##0.00");
    public static final DecimalFormat decimalFormatItem = new DecimalFormat("#0");

    @EventHandler
    public void onClickInventory(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();

        try{
            if(e.getClickedInventory().getItem(26).equals(info())) {

                e.setCancelled(true);

                if(!p.getInventory().contains(e.getCurrentItem().getType())) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lShopkeeper &8» " +
                            "&cYou don't have any of those items to sell!"));
                    p.closeInventory();
                    e.setCancelled(true);
                    return;
                }

                Material material = e.getCurrentItem().getType();
                double price = Prices.get().getDouble(e.getCurrentItem().getType().getKey().getKey().toUpperCase());

                int selling = 1;
                if(e.getClick().equals(ClickType.SHIFT_LEFT)) {
                    selling = 64;
                }
                else if(e.getClick().equals(ClickType.RIGHT)) {
                    selling = 16;
                }
                else if(e.getClick().equals(ClickType.MIDDLE)) {
                    selling = 2368;
                }

                int amount = 0;
                for(int i = 0; i < p.getInventory().getSize(); i++) {
                    if(p.getInventory().getItem(i) != null && p.getInventory().getItem(i).getType().equals(material)) {
                        int stackSize = p.getInventory().getItem(i).getAmount();
                        if(amount + stackSize > selling) {
                            ItemStack stack = new ItemStack(p.getInventory().getItem(i));
                            stack.setAmount(stackSize - (selling - amount));
                            p.getInventory().setItem(i, stack);
                            amount = selling;
                            break;
                        }
                        else {
                            amount += stackSize;
                            p.getInventory().setItem(i, new ItemStack(Material.AIR));
                        }
                    }
                }
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lShopkeeper &8» " +
                        "&aYou sold " + decimalFormatItem.format(amount) + "x &7&o" + e.getCurrentItem().getType() + "&r&7 to the Admin Shop for &a$" + decimalFormat.format(amount * price) + "&7!"));

                HungerShop.vault.depositPlayer(p, price * amount);

            }
        } catch (Exception exception) {
            //.
        }

    }

    public ItemStack info() {
        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta meta = info.getItemMeta();
        meta.setDisplayName(ChatColor.BOLD + "Sell Your Items!");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Left: Sell 1");
        lore.add(ChatColor.GRAY + "Right: Sell 16");
        lore.add(ChatColor.GRAY + "Shift+Left: Sell 64");
        lore.add(ChatColor.GRAY + "Middle: Sell All");
        meta.setLore(lore);
        info.setItemMeta(meta);
        return info;
    }

}
